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
    const loginBtn = await page.$('button:has-text("登录"), button[type="submit"]');
    if (loginBtn) {
      await loginBtn.click();
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(2000);
    }
    await page.screenshot({ path: ss + 'final-01-login.png' });
    
    console.log('2. Go to customers...');
    const customerMenu = await page.$('text=客户管理');
    if (customerMenu) {
      await customerMenu.click();
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(1500);
    }
    await page.screenshot({ path: ss + 'final-02-list.png' });
    
    console.log('3. Click add...');
    const addBtn = await page.$('button:has-text("新增")');
    if (addBtn) {
      await addBtn.click();
      await page.waitForTimeout(1000);
    }
    await page.screenshot({ path: ss + 'final-03-form.png' });
    
    console.log('4. Fill form...');
    await page.fill('input[placeholder="请输入客户名称"]', '张三');
    await page.fill('input[placeholder="请输入联系电话"]', '13800138000');
    await page.fill('input[placeholder="请输入邮箱"]', 'zhangsan@example.com');
    await page.fill('input[placeholder="请输入联系人姓名"]', '张三');
    
    console.log('5. Submit...');
    const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存")');
    if (submitBtn) {
      await submitBtn.click();
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(2000);
    }
    
    console.log('6. Capture result...');
    await page.screenshot({ path: ss + 'final-04-success.png' });
    await page.waitForTimeout(1000);
    await page.screenshot({ path: ss + 'final-05-customer-list.png' });
    
    const content = await page.content();
    if (content.includes('张三')) {
      console.log('✅ SUCCESS! Customer "张三" is visible!');
    } else {
      console.log('⚠️ Customer not found in page');
    }
    
  } catch (e) {
    console.error('Error:', e.message);
    await page.screenshot({ path: ss + 'final-error.png' });
  } finally {
    await browser.close();
  }
})();
