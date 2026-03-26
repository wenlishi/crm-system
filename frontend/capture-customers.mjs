import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  try {
    // 登录
    console.log('Logging in...');
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
    
    // 进入客户管理
    console.log('Navigating to customers...');
    const customerLink = await page.$('text=客户管理, text=客户');
    if (customerLink) {
      await customerLink.click();
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(1500);
    }
    
    // 截图顾客列表
    console.log('Capturing customer list...');
    await page.screenshot({ path: '../../screenshots/crm-customers-list-final.png', fullPage: true });
    
    // 检查是否有"张三"这个顾客
    const pageContent = await page.content();
    if (pageContent.includes('张三')) {
      console.log('✅ Found customer "张三" in the list!');
    } else {
      console.log('⚠️ Customer "张三" not visible, may need to add');
    }
    
    console.log('Screenshot saved!');
    
  } catch (e) {
    console.error('Error:', e.message);
  } finally {
    await browser.close();
  }
})();
