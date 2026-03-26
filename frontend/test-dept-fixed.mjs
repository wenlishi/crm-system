import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  console.log('登录...');
  await page.goto('http://localhost:3000', { waitUntil: 'networkidle' });
  
  const inputs = await page.$$('input[type="text"]');
  if (inputs.length > 0) await inputs[0].fill('admin');
  const pwdInput = await page.$('input[type="password"]');
  if (pwdInput) await pwdInput.fill('admin123');
  const loginBtn = await page.$('button:has-text("登录")');
  if (loginBtn) {
    await loginBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  
  console.log('进入部门管理...');
  const deptMenu = await page.$('text=部门管理');
  if (deptMenu) {
    await deptMenu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  
  console.log('截图...');
  await page.screenshot({ path: '../../screenshots/dept-fixed.png', fullPage: true });
  
  // 检查是否有错误提示
  const errorElements = await page.$$('.el-message--error, [class*="error-message"]');
  if (errorElements.length > 0) {
    console.log('❌ 发现错误提示');
  } else {
    console.log('✅ 部门管理页面正常');
  }
  
  await browser.close();
})();
