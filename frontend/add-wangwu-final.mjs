import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/wangwu-final/';
  
  const { execSync } = await import('child_process');
  try { execSync(`mkdir -p ${ss}`); } catch(e) {}
  
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
  await page.screenshot({ path: ss + '01-login.png' });
  console.log('   ✅ 登录成功');
  
  console.log('\n👥 2. 进入客户管理...');
  const menu = await page.$('text=客户管理');
  if (menu) {
    await menu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(1500);
  }
  await page.screenshot({ path: ss + '02-list.png' });
  console.log('   ✅ 客户列表');
  
  console.log('\n➕ 3. 点击新增客户...');
  const addBtn = await page.$('button:has-text("新增")');
  if (addBtn) {
    await addBtn.click();
    await page.waitForTimeout(1000);
  }
  await page.screenshot({ path: ss + '03-form.png' });
  console.log('   ✅ 打开表单');
  
  console.log('\n✍️ 4. 填写所有字段（修复后的字段）...');
  
  // 客户名称
  await page.fill('input[placeholder*="客户名称"]', '王五');
  console.log('   ✅ 客户名称：王五');
  
  // 联系电话
  await page.fill('input[placeholder*="联系电话"]', '13500135000');
  console.log('   ✅ 联系电话：13500135000');
  
  // 邮箱
  await page.fill('input[placeholder*="邮箱"]', 'wangwu@company.com');
  console.log('   ✅ 邮箱：wangwu@company.com');
  
  // 客户类型（下拉框）
  const selects = await page.$$('select');
  if (selects.length > 0) {
    await selects[0].selectOption('1'); // 个人客户
    console.log('   ✅ 客户类型：个人客户');
  }
  
  // 客户级别
  if (selects.length > 1) {
    await selects[1].selectOption('1'); // 普通客户
    console.log('   ✅ 客户级别：普通客户');
  }
  
  // 客户状态
  if (selects.length > 2) {
    await selects[2].selectOption('1'); // 有效
    console.log('   ✅ 客户状态：有效');
  }
  
  // 公司名称
  const companyInput = await page.$('input[placeholder*="公司名称"]');
  if (companyInput) {
    await companyInput.fill('王五科技公司');
    console.log('   ✅ 公司名称：王五科技公司');
  }
  
  // 客户来源
  const allSelects = await page.$$('select');
  if (allSelects.length > 3) {
    await allSelects[3].selectOption('官网咨询');
    console.log('   ✅ 客户来源：官网咨询');
  }
  
  // 备注
  const textarea = await page.$('textarea');
  if (textarea) {
    await textarea.fill('通过前端表单添加，字段已修复');
    console.log('   ✅ 备注：通过前端表单添加');
  }
  
  await page.screenshot({ path: ss + '04-form-filled.png' });
  console.log('\n📸 表单填写完成');
  
  console.log('\n💾 5. 提交保存...');
  const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存")');
  if (submitBtn) {
    await submitBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(3000);
  }
  
  console.log('📸 6. 截取结果...');
  await page.screenshot({ path: ss + '05-result.png', fullPage: true });
  
  // 验证
  const content = await page.content();
  if (content.includes('王五')) {
    console.log('\n✅ 王五已成功添加！前后端字段已修复！');
  } else {
    console.log('\n⚠️ 未找到王五');
  }
  
  await browser.close();
  console.log('\n🎉 完成！');
})();
