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

test('Зөв нууц үгээр амжилттай нэвтрэх', async ({ page }) => {
  await page.goto('/');
  await login(page, 'secret_sauce');

  // Зөв хаяг руу очихоос гадна бүтээгдэхүүний дэлгэц харагдсан байх ёстой.
  await expect(page).toHaveURL('/inventory.html');
  await expect(page.getByText('Products', { exact: true })).toBeVisible();

  await logout(page);
});
