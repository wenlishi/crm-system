import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/liusen/';
  
  console.log('登录系统...');
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
  // 客户名称
  const nameInput = await page.$('input[placeholder*="客户名称"]');
  if (nameInput) {
    await nameInput.fill('刘森');
    await nameInput.press('Tab'); // 触发验证
    console.log('✅ 客户名称：刘森');
  }
  
  // 联系电话
  const phoneInput = await page.$('input[placeholder*="联系电话"]');
  if (phoneInput) {
    await phoneInput.fill('13612345678');
    await phoneInput.press('Tab');
    console.log('✅ 联系电话：13612345678');
  }
  
  // 邮箱
  const emailInput = await page.$('input[placeholder*="邮箱"]');
  if (emailInput) {
    await emailInput.fill('liusen@example.com');
    await emailInput.press('Tab');
    console.log('✅ 邮箱：liusen@example.com');
  }
  
  // 等待下拉框加载
  await page.waitForTimeout(500);
  
  // 客户类型
  const selects = await page.$$('select');
  console.log(`找到 ${selects.length} 个下拉框`);
  
  if (selects.length > 0) {
    await selects[0].selectOption('1');
    console.log('✅ 客户类型：个人客户');
  }
  
  if (selects.length > 1) {
    await selects[1].selectOption('2');
    console.log('✅ 客户级别：VIP 客户');
  }
  
  if (selects.length > 2) {
    await selects[2].selectOption('1');
    console.log('✅ 客户状态：有效');
  }
  
  // 公司名称
  const companyInput = await page.$('input[placeholder*="公司名称"]');
  if (companyInput) {
    await companyInput.fill('刘森贸易公司');
    console.log('✅ 公司名称：刘森贸易公司');
  }
  
  if (selects.length > 3) {
    await selects[3].selectOption('电话咨询');
    console.log('✅ 客户来源：电话咨询');
  }
  
  console.log('提交表单...');
  const submitBtn = await page.$('button:has-text("确定")');
  if (submitBtn) {
    await submitBtn.click();
    console.log('✅ 点击提交按钮');
    
    // 等待响应
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(5000);
  }
  
  console.log('截图...');
  await page.screenshot({ path: ss + '06-final-result.png', fullPage: true });
  
  // 验证
  const content = await page.content();
  if (content.includes('刘森')) {
    console.log('✅ 刘森已在列表中！');
  } else {
    console.log('⚠️ 未找到刘森');
  }
  
  await browser.close();
})();
