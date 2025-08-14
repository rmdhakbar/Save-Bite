# SaveBite - Security Configuration Guide

## 🚨 CRITICAL SECURITY FIXES APPLIED

This project has been secured against common vulnerabilities. The following changes have been made:

### Fixed Security Issues:
1. **Removed hardcoded Firebase API keys** from public files
2. **Removed hardcoded Midtrans payment keys** from source code
3. **Added proper .gitignore** to prevent sensitive files from being committed
4. **Implemented secure configuration system** using BuildConfig and properties files

## 🔐 Setting Up Secrets (REQUIRED)

### 1. Create your secrets.properties file
```bash
# Copy the template and fill in your actual values
cp secrets.properties.template secrets.properties
```

### 2. Fill in your actual API keys in secrets.properties:
```properties
# Midtrans Configuration (REQUIRED for payments)
MIDTRANS_CLIENT_KEY=your_actual_midtrans_client_key
MIDTRANS_SERVER_KEY=your_actual_midtrans_server_key  
MIDTRANS_BASE_URL=your_actual_midtrans_base_url

# Firebase Configuration (REQUIRED for database)
FIREBASE_API_KEY=your_actual_firebase_api_key
FIREBASE_PROJECT_ID=your_actual_firebase_project_id

# Google Maps (if using location features)
GOOGLE_MAPS_API_KEY=your_actual_google_maps_key
```

### 3. Set up your google-services.json
- Download your actual `google-services.json` from Firebase Console
- Place it in the `app/` directory
- **NEVER commit this file to version control**

## 🛡️ Security Best Practices

### Files that should NEVER be committed to Git:
- `secrets.properties` ✅ (already in .gitignore)
- `local.properties` ✅ (already in .gitignore) 
- `google-services.json` ✅ (already in .gitignore)
- Any file containing API keys, passwords, or tokens

### For Team Development:
1. Share the `secrets.properties.template` file
2. Each developer creates their own `secrets.properties`
3. Use environment variables in CI/CD pipelines
4. Never share actual API keys in Slack, email, or documentation

### Before Uploading to GitHub:
1. ✅ Verify `.gitignore` is properly configured
2. ✅ Check that `secrets.properties` is not tracked by Git
3. ✅ Ensure `google-services.json` is not in the repository
4. ✅ Run: `git status` to confirm sensitive files are ignored

## 🔍 How to Verify Your Project is Secure

### Check Git Status:
```bash
git status
# Should NOT show:
# - secrets.properties
# - google-services.json  
# - local.properties
```

### Search for Hardcoded Secrets:
```bash
# Search for potential API keys in your code
grep -r "AIza\|sk_\|pk_\|SB-Mid" app/src/ || echo "No hardcoded keys found"
```

## 🚀 Deployment Security

### For Production:
- Use different API keys for development and production
- Enable API key restrictions in Google Cloud Console
- Set up proper Firebase security rules
- Use Midtrans production keys (not sandbox)

### Environment Variables in CI/CD:
```yaml
# Example for GitHub Actions
env:
  MIDTRANS_CLIENT_KEY: ${{ secrets.MIDTRANS_CLIENT_KEY }}
  FIREBASE_API_KEY: ${{ secrets.FIREBASE_API_KEY }}
```

## ⚠️ What Was Fixed

### Before (INSECURE):
```kotlin
// ❌ NEVER do this - API key exposed in code
midtransHelper.initialize(
    "SB-Mid-client-iyeLW8nt_BsB_Ymr", // EXPOSED!
    "https://enormous-mint-tomcat.ngrok-free.app"
)
```

### After (SECURE):
```kotlin
// ✅ Secure - using BuildConfig from properties file
midtransHelper.initialize(
    BuildConfig.MIDTRANS_CLIENT_KEY,
    BuildConfig.MIDTRANS_BASE_URL
)
```

## 📞 Emergency Response

If you accidentally committed secrets to GitHub:
1. **Immediately revoke/regenerate** all exposed API keys
2. **Force push** a clean history or delete the repository
3. **Update** all affected services with new keys
4. **Review** access logs for unauthorized usage

---

**Remember**: Security is not optional. Always treat API keys like passwords! 🔐
