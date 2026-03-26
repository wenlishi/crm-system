import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/';
  
  try {
    console.log('1. Login...');
    await page.goto('http://localhost:3000', { waitUntil: 'networkidle' });
    
    // 登录
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
    
    console.log('2. Go to customers...');
    // 点击客户管理菜单
    const customerMenu = await page.$('text=客户管理, text=客户');
    if (customerMenu) {
      await customerMenu.click();
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(1500);
    }
    
    console.log('3. Click add button...');
    // 点击新增
    const addBtn = await page.$('button:has-text("新增"), button:has-text("添加")');
    if (addBtn) {
      await addBtn.click();
      await page.waitForTimeout(500);
    }
    
    console.log('4. Fill form...');
    // 填写表单 - 使用更精确的选择器
    await page.fill('input[placeholder*="客户名称"], input[aria-label*="客户名称"]', '张三');
    await page.fill('input[placeholder*="手机"], input[aria-label*="手机"]', '13800138000');
    await page.fill('input[placeholder*="邮箱"], input[aria-label*="邮箱"]', 'zhangsan@example.com');
    await page.fill('input[placeholder*="公司"], input[aria-label*="公司"]', '示例科技公司');
    
    console.log('5. Submit...');
    // 提交
    const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存")');
    if (submitBtn) {
      await submitBtn.click();
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(2000);
    }
    
    console.log('6. Capture result...');
    // 截图
    await page.screenshot({ path: ss + 'customer-added-success.png', fullPage: true });
    
    // 验证是否添加成功
    const content = await page.content();
    if (content.includes('张三')) {
      console.log('✅ Customer "张三" added successfully!');
    } else {
      console.log('⚠️ Customer may not be visible yet');
    }
    
  } catch (e) {
    console.error('Error:', e.message);
    await page.screenshot({ path: ss + 'error-detail.png' });
  } finally {
    await browser.close();
  }
})();
