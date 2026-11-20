import { expect, test, type Page } from '@playwright/test';

async function login(page: Page, password: string) {
  // Placeholder болон харагдах нэрээр хайвал XPath шиг DOM-ийн шатлалаас хамаарахгүй.
  await page.getByPlaceholder('Username').fill('standard_user');
  await page.getByPlaceholder('Password').fill(password);
  await page.getByRole('button', { name: 'Login', exact: true }).click();
}

async function logout(page: Page) {
  // Цэсээр бодитоор гарч, нэвтрэх хуудас болон товч буцаж ирснийг шалгана.
  await page.getByRole('button', { name: 'Open Menu', exact: true }).click();
  await page.getByTestId('logout-sidebar-link').click();
  await expect(page).toHaveURL('/');
  await expect(page.getByRole('button', { name: 'Login', exact: true })).toBeVisible();
}

test.beforeEach(async ({ page }) => {
  // Playwright-ийн page fixture тест бүрт шинэ context үүсгэнэ. Нэвтрэлт, сагс тусгаарлагдана.
  await page.goto('/');
});

test.afterEach(async ({ page }) => {
  // Нэвтэрсэн тестийг assertion унасан үед ч цэсээр гаргаж дуусгана.
  // Буруу нууц үгээр нэвтрээгүй бол энэ цэс харагдахгүй.
  if (await page.getByRole('button', { name: 'Open Menu', exact: true }).isVisible()) {
    await logout(page);
  }
});

test('Зөв нууц үгээр амжилттай нэвтрэх', async ({ page }) => {
  await login(page, 'secret_sauce');

  // Зөв хаяг руу очихоос гадна бүтээгдэхүүний дэлгэц харагдсан байх ёстой.
  await expect(page).toHaveURL('/inventory.html');
  await expect(page.getByText('Products', { exact: true })).toBeVisible();
});

test('Буруу нууц үгээр нэвтрэхэд алдаа харуулах', async ({ page }) => {
  await login(page, 'wrong_password');

  // Алдааг хэрэглэгчид харуулж, нэвтрэх хуудаснаас шилжээгүйг батална.
  const error = page.getByRole('alert');
  await expect(error).toBeVisible();
  await expect(error).toHaveText(
    'Epic sadface: Username and password do not match any user in this service',
  );
  await expect(page).toHaveURL('/');
});

test('Нэвтэрсний дараа бараа сагсанд нэмэх', async ({ page }) => {
  await login(page, 'secret_sauce');
  await expect(page).toHaveURL('/inventory.html');

  // Барааны нэрээр хүрээг нь тогтоож, зөвхөн тухайн барааны Add to cart товчийг дарна.
  const backpack = page.getByTestId('inventory-item').filter({
    has: page.getByText('Sauce Labs Backpack', { exact: true }),
  });
  await expect(page.getByTestId('shopping-cart-badge')).toBeHidden();
  await backpack.getByRole('button', { name: 'Add to cart', exact: true }).click();
  await expect(page.getByTestId('shopping-cart-badge')).toHaveText('1');

  // Тоолуураас гадна сагсанд яг сонгосон бараа нэг ширхэг орсныг шалгана.
  await page.getByTestId('shopping-cart-link').click();
  await expect(page).toHaveURL('/cart.html');
  const cart = page.getByTestId('cart-list');
  await expect(cart.getByTestId('inventory-item-name')).toHaveText('Sauce Labs Backpack');
  await expect(cart.getByTestId('item-quantity')).toHaveText('1');
});
