import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/';
  
  console.log('📝 1. 登录系统...');
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
  
  console.log('👥 2. 进入客户管理...');
  const customerMenu = await page.$('text=客户管理');
  if (customerMenu) {
    await customerMenu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(1500);
  }
  await page.screenshot({ path: ss + 'before-add-liming.png' });
  
  console.log('➕ 3. 点击新增客户...');
  const addBtn = await page.$('button:has-text("新增")');
  if (addBtn) {
    await addBtn.click();
    await page.waitForTimeout(1000);
  }
  await page.screenshot({ path: ss + 'add-form-liming.png' });
  
  console.log('✍️ 4. 填写客户信息（所有必填字段）...');
  
  // 获取所有输入框并打印 placeholder
  const allInputs = await page.$$('input[type="text"], input:not([type]), textarea');
  console.log('   找到输入框:', allInputs.length);
  
  // 填写所有可能的字段
  await page.fill('input[placeholder="请输入客户名称"]', '李明');
  await page.fill('input[placeholder="请输入联系电话"]', '13912345678');
  await page.fill('input[placeholder="请输入邮箱"]', 'liming@company.com');
  await page.fill('input[placeholder="请输入联系人姓名"]', '李明');
  
  // 尝试填写公司名称（如果有）
  const companyInput = await page.$('input[placeholder*="公司"], input[placeholder*="单位"]');
  if (companyInput) {
    await companyInput.fill('李明科技公司');
  }
  
  // 选择下拉框（客户类型、来源等）
  const selects = await page.$$('select');
  console.log('   找到下拉框:', selects.length);
  for (let i = 0; i < Math.min(selects.length, 3); i++) {
    try {
      await selects[i].selectOption(i === 0 ? '1' : '1');
    } catch(e) {}
  }
  
  console.log('💾 5. 提交保存...');
  const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存"), button:has-text("提交")');
  if (submitBtn) {
    await submitBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(3000);
  }
  
  console.log('📸 6. 截取添加成功画面...');
  await page.screenshot({ path: ss + 'liming-added-success.png', fullPage: true });
  
  // 验证
  const content = await page.content();
  if (content.includes('李明')) {
    console.log('✅ 李明已成功添加到客户列表！');
  } else {
    console.log('⚠️ 未找到李明，检查是否添加成功');
  }
  
  await browser.close();
  console.log('\n🎉 完成！');
})();
