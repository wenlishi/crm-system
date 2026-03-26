import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  console.log('Login...');
  await page.goto('http://localhost:3000', { waitUntil: 'networkidle' });
  
  const inputs = await page.$$('input[type="text"]');
  if (inputs.length > 0) await inputs[0].fill('admin');
  const pwdInput = await page.$('input[type="password"]');
  if (pwdInput) await pwdInput.fill('admin123');
  const loginBtn = await page.$('button:has-text("登录"), button[type="submit"]');
  if (loginBtn) {
    await loginBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  
  console.log('Go to customers...');
  const customerMenu = await page.$('text=客户管理');
  if (customerMenu) {
    await customerMenu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(1500);
  }
  
  console.log('Capture Zhangsan...');
  await page.screenshot({ path: '../../screenshots/zhangsan-customer.png', fullPage: true });
  
  const content = await page.content();
  if (content.includes('张三')) {
    console.log('✅ Zhangsan found!');
  }
  
  await browser.close();
})();
