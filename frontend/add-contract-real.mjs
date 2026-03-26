import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/contract-test/';
  
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
  
  console.log('\n📄 2. 进入合同管理...');
  const menu = await page.$('text=合同管理');
  if (menu) {
    await menu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(1500);
  }
  await page.screenshot({ path: ss + '02-contract-list.png' });
  console.log('   ✅ 合同列表');
  
  console.log('\n➕ 3. 点击新增合同...');
  const addBtn = await page.$('button:has-text("新增合同")');
  if (addBtn) {
    await addBtn.click();
    await page.waitForTimeout(2000);
  }
  await page.screenshot({ path: ss + '03-add-form.png' });
  console.log('   ✅ 打开表单');
  
  console.log('\n✍️ 4. 填写合同信息...');
  
  // 合同名称
  await page.fill('input[placeholder*="请输入名称"]', '蜀国军粮采购合同');
  console.log('   ✅ 合同名称：蜀国军粮采购合同');
  
  // 选择客户（需要先点击下拉框）
  console.log('   选择客户...');
  await page.click('text=请选择客户');
  await page.waitForTimeout(1000);
  // 选择"张三"（数据库中存在的客户）
  await page.click('text=张三');
  await page.waitForTimeout(500);
  console.log('   ✅ 客户：张三');
  
  // 合同金额
  const amountInput = await page.$('input[type="number"]');
  if (amountInput) {
    await amountInput.fill('500000');
    console.log('   ✅ 合同金额：500000');
  }
  
  // 签订日期
  console.log('   选择签订日期...');
  await page.click('input[placeholder*="签订日期"], input[placeholder*="选择日期"]');
  await page.waitForTimeout(500);
  await page.keyboard.press('Enter');
  await page.waitForTimeout(500);
  console.log('   ✅ 签订日期：今天');
  
  // 备注
  const textarea = await page.$('textarea');
  if (textarea) {
    await textarea.fill('2026 年度军粮采购，通过前端表单添加');
    console.log('   ✅ 备注：已填写');
  }
  
  await page.screenshot({ path: ss + '04-form-filled.png' });
  console.log('\n📸 表单填写完成');
  
  console.log('\n💾 5. 提交保存...');
  const submitBtn = await page.$('button:has-text("确定")');
  if (submitBtn) {
    await submitBtn.click();
    console.log('   ✅ 点击提交');
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(5000);
  }
  
  console.log('\n📸 6. 截取结果...');
  await page.screenshot({ path: ss + '05-result.png', fullPage: true });
  
  const content = await page.content();
  if (content.includes('蜀国') || content.includes('军粮')) {
    console.log('\n✅ 合同已添加！');
  } else {
    console.log('\n⚠️ 未找到合同');
  }
  
  await browser.close();
  console.log('\n🎉 完成！');
})();
