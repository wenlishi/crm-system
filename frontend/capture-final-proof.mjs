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
  
  console.log('截取客户管理...');
  const menu = await page.$('text=客户管理');
  if (menu) {
    await menu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  await page.screenshot({ path: '../../screenshots/final-customer-list-proof.png', fullPage: true });
  
  console.log('截取数据看板...');
  const dashboard = await page.$('text=首页, text=仪表盘');
  if (dashboard) {
    await dashboard.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  await page.screenshot({ path: '../../screenshots/final-dashboard-proof.png', fullPage: true });
  
  await browser.close();
  console.log('✅ 完成！');
})();
