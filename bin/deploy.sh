#!/bin/bash

# Branch deployment script for SpringBootAzureDemo
# Usage: ./bin/deploy.sh <environment>
# Environments: sit, uat
#
# For SIT: Deploys current local branch code to sit branch
# For UAT: Deploys current release tag (that local is checking out) to uat branch
# Note: Hotfix tags should be deployed to UAT, then promoted to prod via PR

set -e

function check_for_changes {
  local branch=$1
  local current_sha=$(git rev-parse HEAD)
  local remote_branch="origin/$branch"
  
  # Check if remote branch exists
  if git ls-remote --heads origin "$branch" | grep -q "refs/heads/$branch"; then
    local remote_sha=$(git ls-remote origin "$branch" | cut -f1)
    
    if [ "$current_sha" == "$remote_sha" ]; then
      echo "⚠️  No changes detected. Current commit ($current_sha) is already on $branch branch."
      echo "   Skipping deployment to avoid unnecessary redeployment."
      return 1  # No changes
    else
      echo "✅ Changes detected. Current commit ($current_sha) differs from $branch ($remote_sha)."
      return 0  # Has changes
    fi
  else
    echo "✅ Branch $branch doesn't exist remotely. Will create it."
    return 0  # New branch, proceed
  fi
}

function update_sit_branch {
  local branch="sit"
  
  echo "🔄 Checking for changes before deploying to $branch..."
  
  # Check if there are actual code changes
  if ! check_for_changes "$branch"; then
    echo ""
    echo "ℹ️  To force deployment even without changes, use:"
    echo "   git commit --allow-empty -m 'chore: force deployment'"
    echo "   ./bin/deploy.sh $branch"
    echo ""
    exit 0  # Exit successfully, but skip deployment
  fi
  
  echo "🚀 Updating $branch branch with current local branch code..."
  git branch -D $branch 2>/dev/null || true
  git branch $branch
  git push -f origin refs/heads/$branch:refs/heads/$branch
  
  echo "✅ Successfully updated $branch branch. Deployment will be triggered."
}

function update_uat_branch {
  local branch=$1
  
  # Check if current HEAD is a release tag
  CURRENT_TAG=$(git describe --tags --exact-match HEAD 2>/dev/null || echo "")
  
  if [ -z "$CURRENT_TAG" ]; then
    echo "❌ Error: UAT deployment requires checking out a release tag first"
    echo "   Current commit: $(git rev-parse HEAD)"
    echo ""
    echo "   Available release tags:"
    git tag -l "v[0-9]*.[0-9]*.[0-9]*" | tail -10
    echo ""
    echo "   To deploy to $branch:"
    echo "   1. Checkout a release tag: git checkout v0.1.0"
    echo "   2. Run deployment: ./bin/deploy.sh $branch"
    exit 1
  fi
  
  # Validate tag format (should be semantic version: vX.Y.Z)
  if [[ ! "$CURRENT_TAG" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "❌ Error: Tag '$CURRENT_TAG' does not follow semantic version format (vX.Y.Z)"
    echo "   Expected format: v0.1.0, v0.2.0, v1.0.0, etc."
    exit 1
  fi
  
  echo "✅ Release tag detected: $CURRENT_TAG"
  echo "🔄 Checking for changes before deploying to $branch..."
  
  # Check if this tag is already on the target branch
  if git ls-remote --heads origin "$branch" | grep -q "refs/heads/$branch"; then
    REMOTE_SHA=$(git ls-remote origin "$branch" | cut -f1)
    CURRENT_SHA=$(git rev-parse HEAD)
    
    if [ "$CURRENT_SHA" == "$REMOTE_SHA" ]; then
      echo "⚠️  Release tag $CURRENT_TAG is already deployed to $branch branch."
      echo "   Skipping deployment to avoid unnecessary redeployment."
      echo ""
      echo "ℹ️  To force deployment, use:"
      echo "   ./bin/deploy.sh $branch"
      exit 0
    fi
  fi
  
  echo "🚀 Updating $branch branch with release tag $CURRENT_TAG..."
  git branch -D $branch 2>/dev/null || true
  git branch $branch
  git push -f origin refs/heads/$branch:refs/heads/$branch
  
  echo "✅ Successfully updated $branch branch with release tag $CURRENT_TAG."
  echo "   Deployment will be triggered and can continue to PROD after approval."
}

case $1 in
  sit)
    update_sit_branch sit
    ;;
  uat)
    update_uat_branch uat
    ;;
  *)
    echo "Invalid environment. Use: sit or uat"
    echo ""
    echo "Usage:"
    echo "  ./bin/deploy.sh sit     # Deploy current local branch to SIT"
    echo "  ./bin/deploy.sh uat     # Deploy current release tag to UAT (requires git checkout vX.Y.Z)"
    echo ""
    echo "Note: Hotfix tags should be deployed to UAT, then promoted to prod via PR"
    exit 1
    ;;
esac
