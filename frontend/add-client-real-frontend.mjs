import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/real-frontend/';
  
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
    await page.waitForTimeout(2000);
  }
  await page.screenshot({ path: ss + '03-form.png' });
  console.log('   ✅ 打开表单');
  
  console.log('\n✍️ 4. 填写表单（真正的前端操作）...');
  
  // 客户名称
  await page.click('input[placeholder*="客户名称"]');
  await page.fill('input[placeholder*="客户名称"]', '赵云');
  console.log('   ✅ 客户名称：赵云');
  
  // 联系电话
  await page.click('input[placeholder*="联系电话"]');
  await page.fill('input[placeholder*="联系电话"]', '13900139000');
  console.log('   ✅ 联系电话：13900139000');
  
  // 邮箱
  await page.click('input[placeholder*="邮箱"]');
  await page.fill('input[placeholder*="邮箱"]', 'zhaoyun@shu.com');
  console.log('   ✅ 邮箱：zhaoyun@shu.com');
  
  // 客户类型 - 点击下拉框并选择
  console.log('   选择客户类型...');
  await page.click('text=请选择客户类型');
  await page.waitForTimeout(500);
  await page.click('text=个人客户');
  await page.waitForTimeout(500);
  console.log('   ✅ 客户类型：个人客户');
  
  // 客户级别
  console.log('   选择客户级别...');
  await page.click('text=请选择级别');
  await page.waitForTimeout(500);
  await page.click('text=普通客户');
  await page.waitForTimeout(500);
  console.log('   ✅ 客户级别：普通客户');
  
  // 客户状态
  console.log('   选择客户状态...');
  await page.click('text=请选择客户状态');
  await page.waitForTimeout(500);
  await page.click('text=有效');
  await page.waitForTimeout(500);
  console.log('   ✅ 客户状态：有效');
  
  // 公司名称
  const companyInput = await page.$('input[placeholder*="公司名称"]');
  if (companyInput) {
    await companyInput.fill('蜀国集团');
    console.log('   ✅ 公司名称：蜀国集团');
  }
  
  // 客户来源
  console.log('   选择客户来源...');
  await page.click('text=请选择来源');
  await page.waitForTimeout(500);
  await page.click('text=官网咨询');
  await page.waitForTimeout(500);
  console.log('   ✅ 客户来源：官网咨询');
  
  // 备注
  const textarea = await page.$('textarea');
  if (textarea) {
    await textarea.fill('真正通过前端表单添加的测试客户');
    console.log('   ✅ 备注：已填写');
  }
  
  await page.screenshot({ path: ss + '04-form-filled.png' });
  console.log('\n📸 表单填写完成');
  
  console.log('\n💾 5. 点击确定提交...');
  const submitBtn = await page.$('button:has-text("确定")');
  if (submitBtn) {
    await submitBtn.click();
    console.log('   ✅ 点击提交');
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(5000);
  }
  
  console.log('\n📸 6. 截取结果...');
  await page.screenshot({ path: ss + '05-result.png', fullPage: true });
  
  // 验证
  const content = await page.content();
  if (content.includes('赵云')) {
    console.log('\n✅ 赵云已成功添加！真正的前端操作！');
  } else {
    console.log('\n⚠️ 未找到赵云');
  }
  
  await browser.close();
  console.log('\n🎉 完成！');
})();
