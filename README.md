# Лаб 9: Код хамралт — JaCoCo (ScholarshipCalculator)

- Нэр: Г. Үлэмж
- Оюутны код: B232270805
- Хичээл: F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест (2026)

## Орчин

`results/` доторх бүх файлыг үүсгэсэн машин дээрх хоёр командын бүтэн гаралт:

```
$ java -version
openjdk version "25.0.4" 2026-07-21
OpenJDK Runtime Environment (build 25.0.4+-16339643-b508.27)
OpenJDK 64-Bit Server VM (build 25.0.4+-16339643-b508.27, mixed mode)

$ mvn -version
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: /tmp/apache-maven-3.9.9
Java version: 25.0.4, vendor: JetBrains s.r.o., runtime: /opt/android-studio/jbr
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "7.2.14-arch1-1", arch: "amd64", family: "unix"
```

Тэмдэглэл: JDK нь 17 биш 25 хувилбар. `pom.xml`-ийг өөрчлөөгүй: `maven.compiler.release=17` тохиргоогоор compile хийгддэг бөгөөд JaCoCo 0.8.12 энэ JDK дээр хэвийн ажилласан. Maven 3.9.9-ийг системд суулгаагүй, tar архиваас задалж ашигласан.

Бүгдийг `mvn verify` командаар ажиллуулна. JaCoCo тайлан `target/site/jacoco/` хавтсанд (HTML, XML, CSV) үүснэ.

## Хэсэг A: Happy path тест ба эхний хэмжилт

5 хэвийн урсгалын тест: `letterGrade_95_isA`, `letterGrade_85_isB`, `gpa_95and85_is3_5`, `scholarship_highGpaEnoughCredits_is100`, `scholarship_gpa3_1_is50`.

`results/jacoco-A.csv`-ээс:

| Үзүүлэлт | Хамрагдаагүй | Хамрагдсан | Хамралт |
|---|---|---|---|
| Line | 9 | 16 | 16/25 = 64% |
| Branch | 21 | 20 | 20/41 = 48.8% |

JaCoCo тайлан дээрх ажиглалт (`ScholarshipCalculator.java`, мөрийн дугаар нь эх кодынх):

- Улаан (огт ажиллаагүй) мөрүүд: гурван `throw new IllegalArgumentException` (21, 32, 49-р мөр), `letterGrade`-ийн C/D/F буцаалт (25-27), `gpa`-ийн `switch`-ийн C, D, F салаа (39, 40) ба `scholarship`-ийн сүүлийн `return 0` (55). Өөрөөр хэлбэл алдааны бүх зам, A/B-ээс бусад үсгэн дүн тестлэгдээгүй байна.
- Шар (салаа хагас хамрагдсан) мөрүүд: `letterGrade`-ийн хүрээний шалгалт ба `>= 90` (20, 24), `gpa`-ийн null/хоосон шалгалт (31), давталтын нөхцөл (36), `scholarship`-ийн оролтын шалгалт (48) болон `hasViolation`, `>= 3.8 && >= 15`, `>= 3.4 && >= 12`, `>= 3.0` нөхцөлүүд (51-54). Эдгээрт үнэн эсвэл худал талын аль нэг нь хэзээ ч биелээгүй (жишээ нь `&&`, `||`-ийн хоёр дахь нөхцөл шалгагдаагүй).

## Хэсэг B: CFG зурж branch 100% хүрэх

`cfg.png` (эх файл нь `cfg.svg`) нь `scholarship()` функцын CFG. N1-N15 зангилаа нь эх кодын мөртэй таарна, ирмэг бүрийн хажууд түүнийг дайрах тестийн бодит нэр бичигдсэн. `gpa < 0 || gpa > 4 || credits < 0` нөхцөл бүрийг JaCoCo тусдаа салаа гэж тоолдог тул тус тусын зангилаа (N1-N3) болгосон. CFG-г цахим хэлбэрээр зурсан (гараар биш).

Гурван функцэд нэмсэн тестүүд:

- `letterGrade`: `letterGrade_75_isC`, `letterGrade_65_isD`, `letterGrade_30_isF`, `letterGrade_negative_throws`, `letterGrade_above100_throws`
- `gpa`: `gpa_null_throws`, `gpa_empty_throws`, `gpa_allGradeKinds_is2_0` (A, B, C, D, F бүгд нэг дуудлагад, тиймээс `switch`-ийн салаа бүр ажиллана)
- `scholarship`: `scholarship_negativeGpa_throws`, `scholarship_gpaAbove4_throws`, `scholarship_negativeCredits_throws` (`||`-ийн нөхцөл бүрт нэг `assertThrows`), `scholarship_violation_is0`, `scholarship_highGpaLowCredits_is70`, `scholarship_gpa3_5Credits12_is70`, `scholarship_gpa3_5Credits10_is50`, `scholarship_lowGpa_is0`

Нийт шаардагдсан тест: **21** (`results/mvn-verify-B.txt`-д `Tests run: 21`).
`results/jacoco-B.csv`: `BRANCH_MISSED = 0` (41/41 хамрагдсан), `LINE_MISSED = 0` (25/25 хамрагдсан).

## Хэсэг C: Давталтын заагийн тестүүд

`gpa()` доторх давталтад 0 / 1 / 2+ дүрмийг хэрэгжүүлсэн:

| Давталтын тоо | Тест |
|---|---|
| 0 | `gpa_empty_throws` ба `gpa_null_throws` (Хэсэг B-д нэмсэн; давталтын бие огт ажиллахгүй) |
| 1 | `gpa_oneScore_is4_0`: `gpa([95]) == 4.0` |
| 2+ | `gpa_manyScores_is2_5`: `gpa([95, 85, 75, 65]) == 2.5` |

Хэсэг C-ийн дараа: 23 тест, хамрагдаагүй салаа/мөр 0 хэвээр (`results/mvn-verify-C.txt`, `results/jacoco-C.csv`).

Эдгээр тест хамралтын тоог өсгөхгүй, учир нь давталт аль хэдийн 100% хамрагдсан. Гэхдээ хамралт нь мөр, салаа ядаж нэг удаа ажилласныг л хэлдэг тул давталт 0, 1, олон элементтэй үед зөв ажиллаж буйг (жишээ нь `scores.length`-д хуваах, нэгээр алдах, голч буруу бодогдох) зөвхөн ийм тест баталгаажуулна.

