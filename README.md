**Оюутны нэр** `<ОЮУТНЫ НЭР>`

**Оюутны код** `<КОД>`

# Лаборатори №1 — UI автомат тест Playwright

F.CSA313 · Программ хангамжийн чанарын баталгаа ба тест

SauceDemo дэлгүүр дээр TypeScript, Playwright болон Chromium ашиглан гурван тест ажиллуулсан.

1. Зөв нууц үгээр нэвтэрч бүтээгдэхүүний хуудас нээгдсэнийг шалгана.
2. Буруу нууц үг оруулахад алдааны мессеж харагдаж, нэвтрэх хуудсанд хэвээр үлдсэнийг шалгана.
3. Нэвтэрсний дараа Sauce Labs Backpack барааг сагсанд нэмж, тоолуур, нэр, ширхэгийг шалгана.

Тест бүр шинэ browser context-тэй. Нэвтэрсэн тестийн дараа цэсээр гарч, нэвтрэх хуудас буцаж нээгдсэнийг шалгана. Тестүүдийг хамтад нь болон нэг нэгээр нь ажиллуулахад бүгд амжилттай болсон.

## Суулгах, ажиллуулах

Node.js 20 буюу түүнээс дээш хувилбар, npm болон SauceDemo-д хандах интернэт хэрэгтэй. Энэ ажлыг Arch Linux дээр Node.js v22.23.4, Playwright 1.65.1 ашиглан шалгасан.

```bash
npm ci --cache .npm-cache
PLAYWRIGHT_BROWSERS_PATH=0 npx playwright install
npx playwright test
npx playwright test --trace on
```

`PLAYWRIGHT_BROWSERS_PATH=0` нь браузер болон FFmpeg-ийг төслийн `node_modules` дотор суулгана. Суулгагч Arch Linux дээр Ubuntu 24.04-ийн хувилбарыг татаж, системийн сангууд дутуу байгааг анхааруулсан. Тестүүдийг татсан Chromium Headless Shell дээр ажиллуулсан. Тестийн тохиргоонд trace болон видео бичлэгийг үргэлж идэвхжүүлсэн.

Төслийг анх үүсгэхдээ `npm init -y`, `npm install --save-dev --cache .npm-cache @playwright/test @types/node` командыг ашигласан. Тестийн тусгаарлалтыг дараах командаар шалгасан.

```bash
for name in 'Зөв нууц үгээр амжилттай нэвтрэх' 'Буруу нууц үгээр нэвтрэхэд алдаа харуулах' 'Нэвтэрсний дараа бараа сагсанд нэмэх'; do
  npx playwright test --grep "$name" --reporter=list --output .lab-logs/isolated
done
```

## Тайлан, trace ба видео

- [HTML тайлан](artifacts/playwright-report/index.html) — 3 тест амжилттай.
- [Амжилттай сагсны тестийн trace](artifacts/cart-pass-trace.zip)
- [Амжилттай сагсны тестийн видео](artifacts/cart-pass.webm)
- [Санаатай унагасан тестийн trace](artifacts/cart-assertion-failure-trace.zip)

Тайлан ба trace-ийг төслийн хавтсаас нээнэ.

```bash
npx playwright show-report artifacts/playwright-report --host 127.0.0.1 --port 9411
PLAYWRIGHT_BROWSERS_PATH=0 npx playwright show-trace --host 127.0.0.1 --port 9410 artifacts/cart-assertion-failure-trace.zip
```

Сагсны тоолуурын `toHaveText('1')` шалгалтыг түр `toHaveText('2')` болгон өөрчилж, `npx playwright test --grep 'бараа сагсанд нэмэх' --trace on` командыг ажиллуулахад нэг тест унасан. Trace Viewer-ийн **Errors** хэсэгт хүлээсэн утга `2`, бодит утга `1`, хүлээлтийн хугацаа 5000 мс гэж харагдсан. **DOM Snapshot** дээр сагсны тоолуур `1`, нэмсэн барааны товч `Remove` болсон байв. Шалгалтыг `1` болгон сэргээж бүх тестийг дахин ажиллуулахад гурвуулаа амжилттай болсон.

## Playwright ба Selenium-ийн талаарх ажиглалт

1. Энэ туршилтад лабораторийн зааварт дурдсан Selenium-ийн explicit wait-тэй харьцуулахад Playwright-ийн `click()` болон `expect`-д тусдаа хүлээлт бичих хэрэг гарсангүй.
2. Санаатай унагасан шалгалтын trace дээр `toHaveText()` таван секундийн турш утгыг дахин шалгаж байгааг харсан.
3. Браузерууд болон видео хадгалах FFmpeg нэг `playwright install` командаар татагдсан тул энэ ажилд WebDriver тусад нь тохируулах алхам байгаагүй.
4. Selenium IDE-тэй адил үйлдэл бичих зориулалттай Codegen-ээр SauceDemo-д нэвтэрч гарах алхмуудыг TypeScript код болгон гаргасан.
5. Codegen нэвтрэх талбар, товчийг `data-test` атрибуттай CSS locator-оор олсон бол өөрийн тестэд `getByPlaceholder`, `getByRole` ашиглаж, шалгалтуудыг нэмж бичсэн.
6. Товчны харагдах нэр болон тестийн ID-д тулгуурласан locator уншихад ойлгомжтой, DOM-ийн шатлалд тулгуурласан XPath-аас хараат байдал багатай санагдсан.
7. Selenium-ийн лог, дэлгэцийн зурагтай харьцуулахад Trace Viewer дээр алдааны мөр, өмнөх үйлдэл, тухайн агшны DOM-ийг нэг дор харах боломж надад илүү хэрэгтэй байв.

Харьцуулалтыг лабораторийн зааварт тайлбарласан Selenium-ийн ажиллагаа болон дээрх Playwright туршилтад үндэслэсэн. Codegen-ийг дэлгэцийн цонх нээлгүй ажиллуулахдаа `.lab-logs/codegen-chromium` скриптээр `exec /usr/bin/chromium --remote-debugging-port=9420 "$@"` ажиллуулж, нэвтрэх болон гарах үйлдлийг CDP-ээр дамжуулсан.

```bash
PLAYWRIGHT_BROWSERS_PATH=0 PWTEST_CLI_HEADLESS=1 PWTEST_CLI_EXECUTABLE_PATH="$PWD/.lab-logs/codegen-chromium" npx playwright codegen --target=playwright-test --output .lab-logs/codegen.spec.ts https://www.saucedemo.com/
```
