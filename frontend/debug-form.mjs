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
  
  console.log('Click add...');
  const addBtn = await page.$('button:has-text("新增")');
  if (addBtn) {
    await addBtn.click();
    await page.waitForTimeout(1000);
  }
  
  console.log('Screenshot form...');
  await page.screenshot({ path: '../../screenshots/debug-form.png', fullPage: true });
  
  // 获取所有输入框
  const allInputs = await page.$$('input, textarea');
  console.log('Found inputs:', allInputs.length);
  for (let i = 0; i < allInputs.length; i++) {
    const placeholder = await allInputs[i].getAttribute('placeholder');
    const ariaLabel = await allInputs[i].getAttribute('aria-label');
    const type = await allInputs[i].getAttribute('type');
    console.log(`  ${i}: type=${type}, placeholder=${placeholder}, aria=${ariaLabel}`);
  }
  
  await browser.close();
})();
