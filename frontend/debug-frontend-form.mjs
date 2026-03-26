import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  // 捕获控制台错误
  page.on('console', msg => {
    if (msg.type() === 'error') {
      console.log('🔴 JS Error:', msg.text());
    }
  });
  
  page.on('pageerror', err => {
    console.log('🔴 Page Error:', err.message);
  });
  
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
  
  console.log('进入客户管理...');
  const menu = await page.$('text=客户管理');
  if (menu) {
    await menu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(1500);
  }
  
  console.log('点击新增...');
  const addBtn = await page.$('button:has-text("新增")');
  if (addBtn) {
    await addBtn.click();
    await page.waitForTimeout(1000);
  }
  
  console.log('填写表单...');
  await page.fill('input[placeholder="请输入客户名称"]', '王强');
  await page.fill('input[placeholder="请输入联系电话"]', '13800138002');
  await page.fill('input[placeholder="请输入邮箱"]', 'wangqiang@test.com');
  
  console.log('提交...');
  const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存")');
  if (submitBtn) {
    console.log('点击提交按钮...');
    await submitBtn.click();
    
    // 等待响应
    try {
      await page.waitForLoadState('networkidle', { timeout: 10000 });
      await page.waitForTimeout(3000);
    } catch(e) {
      console.log('⚠️ 等待超时');
    }
  }
  
  console.log('截图...');
  await page.screenshot({ path: '../../screenshots/debug-form-submit.png', fullPage: true });
  
  // 检查网络请求
  console.log('\n检查是否有 API 请求...');
  
  await browser.close();
})();
