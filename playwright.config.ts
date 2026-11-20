import { defineConfig, devices } from '@playwright/test';

// Браузер, FFmpeg-ийг төслийн node_modules дотор суулгана.
process.env.PLAYWRIGHT_BROWSERS_PATH ??= '0';

export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    trace: 'on',
    video: 'on',
    baseURL: 'https://www.saucedemo.com',
    // SauceDemo нь тестэд зориулсан data-test атрибут ашигладаг.
    testIdAttribute: 'data-test',
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
});
