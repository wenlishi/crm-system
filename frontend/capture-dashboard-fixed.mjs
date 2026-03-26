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
  
  console.log('进入数据看板...');
  const dashboardMenu = await page.$('text=首页, text=仪表盘');
  if (dashboardMenu) {
    await dashboardMenu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  
  console.log('截图...');
  await page.screenshot({ path: '../../screenshots/dashboard-no-error.png', fullPage: true });
  
  // 检查控制台错误
  const content = await page.content();
  if (content.includes('错误') || content.includes('Error') || content.includes('Exception')) {
    console.log('⚠️ 页面仍有错误提示');
  } else {
    console.log('✅ 数据看板正常，无报错');
  }
  
  await browser.close();
})();
