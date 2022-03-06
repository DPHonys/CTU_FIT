# BOOLEOVSKÝ MODEL

Tento projekt je semestrální prací pro předmět „Vyhledávání na webu a v multimediálních databázích“ (BI-VWM). Projekt implementuje Booleovský model pro získávání informací. Projekt je rozdělen do několik sub projektů a využívá gradle.

## bm-scraper

Sub projekt, který má za úkol vytvoření data setu různých článků z Wikipedie. Využívá odkaz, který uživatele přesměruje na random článek na Wikipedii ([Wikipedia Special Random](https://en.wikipedia.org/wiki/Wikipedia:Special:Random)). Ze stránky článku se pak vezme několik odstavců. Odstavce společně s názvem a odkazem na článek jsou uloženy do souboru. Pro každý článek je jeden soubor. 

## bm-preprocess

Tento sub projekt se zabývá předzpracováním data setu. Předzpracování zahrnuje načtení souboru obsahující článek, zpracování textu a extrakce jeho termů. Data článku, jeho termy a relace jsou zapsány do databáze.

## bm-web-app 

V tomto sub projektu je realizovaná webová aplikace, která umožňuje uživateli provádět booleovský dotaz nad databází. Nejdříve dojde ke kontrole, zda dotaz splňuje pravidla definované gramatiky. Pokud je dotaz v pořádku, tak je proveden nad databází a výsledek je pak vyobrazen uživateli. Dále aplikace umožnuje uživateli si prohlédnou jednotlivé termy a články, které se v databázi nacházejí.

## bm-db-utils

Sdílená knihovna pro sub projekty, která obsahuje nástroje pro komunikaci s databází.