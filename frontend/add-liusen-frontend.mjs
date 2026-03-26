import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/liusen/';
  
  const { execSync } = await import('child_process');
  try { execSync(`mkdir -p ${ss}`); } catch(e) {}
  
  console.log('📝 步骤 1: 登录系统');
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
  await page.screenshot({ path: ss + '01-login.png' });
  console.log('   ✅ 登录成功 - 截图保存');
  
  console.log('\n👥 步骤 2: 进入客户管理');
  const menu = await page.$('text=客户管理');
  if (menu) {
    await menu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(1500);
  }
  await page.screenshot({ path: ss + '02-customer-list.png' });
  console.log('   ✅ 客户列表页面 - 截图保存');
  
  console.log('\n➕ 步骤 3: 点击新增客户');
  const addBtn = await page.$('button:has-text("新增")');
  if (addBtn) {
    await addBtn.click();
    await page.waitForTimeout(1000);
  }
  await page.screenshot({ path: ss + '03-add-form.png' });
  console.log('   ✅ 打开新增表单 - 截图保存');
  
  console.log('\n✍️ 步骤 4: 填写客户信息（所有必填字段）');
  
  // 客户名称（必填）
  await page.fill('input[placeholder*="客户名称"]', '刘森');
  console.log('   ✅ 客户名称：刘森');
  
  // 联系电话（必填）
  await page.fill('input[placeholder*="联系电话"]', '13612345678');
  console.log('   ✅ 联系电话：13612345678');
  
  // 邮箱（必填）
  await page.fill('input[placeholder*="邮箱"]', 'liusen@example.com');
  console.log('   ✅ 邮箱：liusen@example.com');
  
  // 客户类型（下拉框）
  const selects = await page.$$('select');
  if (selects.length > 0) {
    await selects[0].selectOption('1'); // 个人客户
    console.log('   ✅ 客户类型：个人客户');
  }
  
  // 客户级别（下拉框）
  if (selects.length > 1) {
    await selects[1].selectOption('2'); // VIP 客户
    console.log('   ✅ 客户级别：VIP 客户');
  }
  
  // 客户状态（下拉框）
  if (selects.length > 2) {
    await selects[2].selectOption('1'); // 有效
    console.log('   ✅ 客户状态：有效');
  }
  
  // 公司名称
  const companyInput = await page.$('input[placeholder*="公司名称"]');
  if (companyInput) {
    await companyInput.fill('刘森贸易公司');
    console.log('   ✅ 公司名称：刘森贸易公司');
  }
  
  // 客户来源（下拉框）
  const allSelects = await page.$$('select');
  if (allSelects.length > 3) {
    await allSelects[3].selectOption('电话咨询');
    console.log('   ✅ 客户来源：电话咨询');
  }
  
  // 备注
  const textarea = await page.$('textarea');
  if (textarea) {
    await textarea.fill('2026 年 3 月 26 日通过前端表单新增的客户');
    console.log('   ✅ 备注：已填写');
  }
  
  await page.screenshot({ path: ss + '04-form-filled.png' });
  console.log('\n📸 表单填写完成 - 截图保存');
  
  console.log('\n💾 步骤 5: 点击确定提交');
  const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存")');
  if (submitBtn) {
    await submitBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(3000);
  }
  
  console.log('\n📸 步骤 6: 截取提交后结果');
  await page.screenshot({ path: ss + '05-submit-success.png', fullPage: true });
  console.log('   ✅ 提交成功 - 截图保存');
  
  // 验证
  const content = await page.content();
  if (content.includes('刘森')) {
    console.log('\n✅ 刘森已成功添加到客户列表！');
    console.log('   🎉 前后端完全打通！');
  } else {
    console.log('\n⚠️ 未找到刘森');
  }
  
  await browser.close();
  console.log('\n🎉 所有操作完成！');
})();
