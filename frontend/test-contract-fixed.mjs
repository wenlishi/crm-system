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
  
  console.log('进入合同管理...');
  const contractMenu = await page.$('text=合同管理');
  if (contractMenu) {
    await contractMenu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  
  console.log('截图...');
  await page.screenshot({ path: '../../screenshots/contract-fixed.png', fullPage: true });
  
  const content = await page.content();
  if (content.includes('错误') || content.includes('Error')) {
    console.log('⚠️ 页面仍有错误');
  } else {
    console.log('✅ 合同管理页面正常');
  }
  
  await browser.close();
})();
