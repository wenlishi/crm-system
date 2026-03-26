import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/';
  
  try {
    console.log('1. Login...');
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
    
    console.log('2. Go to customers...');
    const menu = await page.$('text=客户管理');
    if (menu) {
      await menu.click();
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(1500);
    }
    await page.screenshot({ path: ss + 'before-add.png' });
    
    console.log('3. Add customer...');
    const addBtn = await page.$('button:has-text("新增")');
    if (addBtn) {
      await addBtn.click();
      await page.waitForTimeout(500);
    }
    
    // 填写表单
    await page.fill('input[placeholder="请输入客户名称"]', '成功顾客');
    await page.fill('input[placeholder="请输入联系电话"]', '13500135000');
    await page.fill('input[placeholder="请输入邮箱"]', 'success@test.com');
    
    console.log('4. Submit...');
    const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存")');
    if (submitBtn) {
      await submitBtn.click();
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(3000);
    }
    
    console.log('5. Capture...');
    await page.screenshot({ path: ss + 'after-add-attempt.png' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: ss + 'final-list.png' });
    
    const content = await page.content();
    if (content.includes('成功顾客')) {
      console.log('✅ SUCCESS!');
    } else {
      console.log('⚠️ May have failed');
    }
    
  } catch (e) {
    console.error('Error:', e.message);
  } finally {
    await browser.close();
  }
})();
