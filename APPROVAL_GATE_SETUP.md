# Production Approval Gate Setup Guide

## Overview

This guide explains how to set up the production approval gate for SpringPipelineDemo. When enabled, the production deployment will pause after UAT deployment and wait for manual approval before proceeding.

## Why You Don't See Approval Pending

**Current Status:** Approval gate is **disabled** by default.

When `ENABLE_PROD_APPROVAL_GATE` is not set or set to `"false"`:
- Production deployment uses `production-auto` environment (no approval required)
- Deployment proceeds automatically after UAT completes
- No approval pending notification appears

When `ENABLE_PROD_APPROVAL_GATE` is set to `"true"`:
- Production deployment uses `production` environment (requires approval)
- Deployment pauses after UAT and waits for approval
- Approval notification appears in GitHub Actions

## Step-by-Step Setup Instructions

### Step 1: Create GitHub Repository Variable

1. **Navigate to Repository Settings**
   - Go to your GitHub repository: `https://github.com/YOUR_USERNAME/SpringPipelineDemo`
   - Click on **Settings** tab (top navigation)

2. **Access Secrets and Variables**
   - In the left sidebar, click **Secrets and variables**
   - Click **Actions** submenu

3. **Create New Variable**
   - Click on the **Variables** tab (not Secrets)
   - Click **New repository variable** button

4. **Configure the Variable**
   - **Name:** `ENABLE_PROD_APPROVAL_GATE`
   - **Value:** `true` (to enable) or `false` (to disable)
   - Click **Add variable**

   **Note:** 
   - `true` = Enable approval gate (requires approval)
   - `false` = Disable approval gate (auto-deploy, GitHub Free compatible)
   - If variable doesn't exist, defaults to `false` (auto-deploy)

### Step 2: Create Production Environment

1. **Navigate to Environments**
   - In the same **Settings** page
   - In the left sidebar, click **Environments** (under "Secrets and variables")

2. **Create New Environment**
   - Click **New environment** button
   - Enter environment name: `production` (must be exactly `production`)
   - Click **Configure environment**

3. **Configure Required Reviewers**
   - Scroll down to **Required reviewers** section
   - Click **Add required reviewers**
   - Enter GitHub usernames or select teams who should approve production deployments
   - Click **Save protection rules**

   **Important:**
   - Reviewers must have **Write** permission or higher on the repository
   - You can add multiple reviewers (any one can approve)
   - You can add teams instead of individual users

4. **Optional: Configure Deployment Branches**
   - Under **Deployment branches**, you can restrict which branches can deploy
   - Default: All branches (recommended for flexibility)
   - You can restrict to specific branches if needed

5. **Save Configuration**
   - Click **Save protection rules** button at the bottom

### Step 3: Verify Configuration

1. **Check Variable**
   - Go to: **Settings** → **Secrets and variables** → **Actions** → **Variables** tab
   - Verify `ENABLE_PROD_APPROVAL_GATE` exists and is set to `true`

2. **Check Environment**
   - Go to: **Settings** → **Environments**
   - Verify `production` environment exists
   - Verify it has required reviewers configured

### Step 4: Test the Approval Gate

1. **Deploy to UAT**
   ```bash
   git checkout v1.0.0  # or any release tag
   ./bin/deploy.sh uat
   ```

2. **Monitor UAT Deployment**
   - Go to **Actions** tab in GitHub
   - Watch the UAT deployment complete

3. **Check for Approval Request**
   - After UAT deployment succeeds, the production job should start
   - The job will show **"Waiting"** or **"Review required"** status
   - You should see a notification or banner indicating approval is needed

4. **Approve Deployment**
   - Click on the workflow run
   - Look for the **"production"** environment job
   - Click **"Review deployments"** button
   - Click **"Approve and deploy"** (or **"Reject"** if needed)
   - Add optional comment
   - Click **"Approve and deploy"** to confirm

5. **Verify Production Deployment**
   - After approval, production deployment should proceed automatically
   - Monitor the deployment progress

## How It Works

### When Approval Gate is Enabled (`ENABLE_PROD_APPROVAL_GATE = "true"`)

```
1. Deploy to UAT
   ↓
2. UAT deployment completes successfully
   ↓
3. Production deployment job starts
   ↓
4. ⏸️  PAUSES - Waiting for approval
   - Job shows "Waiting" status
   - Notification sent to required reviewers
   - Workflow is paused at environment protection gate
   ↓
5. Reviewer approves deployment
   ↓
6. Production deployment proceeds
   ↓
7. Production deployment completes
```

### When Approval Gate is Disabled (`ENABLE_PROD_APPROVAL_GATE = "false"` or not set)

```
1. Deploy to UAT
   ↓
2. UAT deployment completes successfully
   ↓
3. Production deployment job starts
   ↓
4. ✅ Continues automatically (no pause)
   ↓
5. Production deployment completes
```

## Troubleshooting

### Issue: No approval pending notification

**Possible Causes:**
1. `ENABLE_PROD_APPROVAL_GATE` variable is not set or set to `false`
2. `production` environment doesn't exist
3. Required reviewers not configured
4. You're not in the required reviewers list

**Solution:**
1. Verify variable is set to `true`: **Settings** → **Secrets and variables** → **Actions** → **Variables**
2. Verify environment exists: **Settings** → **Environments** → Check for `production`
3. Verify reviewers configured: **Settings** → **Environments** → `production` → **Required reviewers**
4. Add yourself to required reviewers if needed

### Issue: Approval button not visible

**Possible Causes:**
1. You don't have Write permission on the repository
2. You're not in the required reviewers list
3. Environment protection not properly configured

**Solution:**
1. Check your repository permissions (must be Write or Admin)
2. Ask repository admin to add you to required reviewers
3. Verify environment protection rules are saved

### Issue: Both jobs run (with approval and auto)

**This should not happen** - only one job should run based on the variable value.

**Check:**
1. Verify `ENABLE_PROD_APPROVAL_GATE` variable value (should be exactly `true` or `false`)
2. Check workflow logs to see which condition evaluated
3. If variable is not set, it defaults to auto mode

### Issue: Workflow fails with "environment not found"

**Solution:**
1. Create the `production` environment: **Settings** → **Environments** → **New environment**
2. Name it exactly `production` (case-sensitive)
3. Save the environment (reviewers can be added later)

## Configuration Summary

| Variable | Value | Behavior |
|----------|-------|----------|
| `ENABLE_PROD_APPROVAL_GATE` | `true` | Requires approval in `production` environment |
| `ENABLE_PROD_APPROVAL_GATE` | `false` | Auto-deploy using `production-auto` environment |
| Not set | (defaults to `false`) | Auto-deploy using `production-auto` environment |

## Quick Reference

**Enable Approval Gate:**
1. Set variable: `ENABLE_PROD_APPROVAL_GATE = "true"`
2. Create environment: `production`
3. Add required reviewers to `production` environment

**Disable Approval Gate:**
1. Set variable: `ENABLE_PROD_APPROVAL_GATE = "false"`
2. Or delete the variable (defaults to false)

**Approve Deployment:**
1. Go to **Actions** tab
2. Find the workflow run
3. Click on the run
4. Click **"Review deployments"**
5. Click **"Approve and deploy"**

## Best Practices

1. **Multiple Reviewers:** Add at least 2-3 reviewers for redundancy
2. **Team-Based:** Use teams instead of individual users for easier management
3. **Notifications:** Configure GitHub notifications for approval requests
4. **Documentation:** Document who can approve and the approval process
5. **Audit Trail:** All approvals are logged in GitHub Actions history

## Security Considerations

1. **Minimum Permissions:** Only grant Write permission to trusted reviewers
2. **Audit Logs:** All approvals are logged and visible in Actions history
3. **Time Limits:** Consider adding deployment wait timers (optional)
4. **Branch Protection:** Can restrict which branches can deploy to production

## Example Approval Flow

```
Developer: ./bin/deploy.sh uat
    ↓
UAT Deployment: ✅ Success
    ↓
Production Job: ⏸️  Waiting for approval
    ↓
Reviewer: Receives notification
    ↓
Reviewer: Reviews deployment details
    ↓
Reviewer: Clicks "Approve and deploy"
    ↓
Production Deployment: ✅ Proceeds automatically
    ↓
Production Deployment: ✅ Completes successfully
```

## Notes

- **GitHub Free:** Approval gates work on GitHub Free tier (no environment reviewers limit)
- **Public Repos:** Approval gates work on public repositories
- **Private Repos:** Approval gates work on private repositories (requires GitHub Pro/Team for some features)
- **Environment Names:** Must be exactly `production` (case-sensitive) for approval gate to work
