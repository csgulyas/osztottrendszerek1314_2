osztottrendszerek13142
======================

Osztott rendszerek 2013-14/2 beadandó


Az alábbi leírásban szereplő mindkét osztály a  bead.dht  nevű csomagba kerüljön.
A leírásban a 2^n jelölés "kettő az n-ediken" jelentésben szerepel,
a számításainkat pedig modulo 2^16 végezzük.

-----------------------------

A beadandó feladat olyan program elkészítése,
amely egy egyszerűsített működésű
elosztott hasítótáblát (DHT, distributed hash table)
valósít meg az alábbiakban leírtak szerint.


A program működése a következő.
A DHTMain osztály indításakor egy számot kap parancssori paraméterként (n),
és elindít egy szervert a 65432 porton.
Ezután n példányban futtatjuk a DHTNode osztályt;
ezek mindegyike parancssori paraméterben kap egy portszámot,
kapcsolódik a DHTMain-hez, átküldi neki a portszámot,
majd bontja a kapcsolatot vele.

Amikor a DHTMain szerver mind az n darab DHTNode portját ismeri,
véletlenszerűen kiválaszt mindegyikhez egy egyedi azonosítót a 0..65535 intervallumból.
Ezután a DHTMain sorban kapcsolódik mindegyik DHTNode-hoz,
és átküldi mindegyiknek a következő információkat:

    - annak az azonosító-intervallumnak az alsó és felső határát,
      amelyik azonosítójú fájlokért a DHTNode felelős
        - az alsó határ értéke a sorrendben megelőző DHTNode azonosítója plusz egy
        - a felső határ értéke a DHTNode saját azonosítója

    - a DHTNode-hoz tartozó mutatótáblát
        - ez pontosan 16 bejegyzést tartalmaz
        - egy bejegyzés két adatból áll
            - egy azonosítóból (ami valamelyik DHTNode-hoz tartozik) és
            - egy portszámból (az előbbi azonosítójú DHTNode portja)
        - a mutatótábla szerkezetét lásd lent

A portszámuk elküldése után a DHTNode-ok elindítanak egy szervert a portjukon,
és fogadják rajta keresztül a DHTMain kezdeti üzenetét.
Ezután a DHTNode sorban fogadja a további kapcsolatokat;
a kapcsolatok kezeléséhez nem szükséges párhuzamosságot használni.
Ezek a kapcsolatok érkezhetnek kliensektől (pl. telnet használatával),
illetve a DHTNode-ok is kapcsolódhatnak egymáshoz.
A DHTNode a következő formátumú szöveges üzeneteket képes kezelni.

    upload <fájlnév>
    <fájl tartalma soronként>

        A fájl feltöltése az elosztott rendszerbe.
        A fájltartalom végét a kommunikációs csatorna bezárása jelzi.

        Ha a fájlért ez a szerver a felelős
        (a fájl nevéből a mellékelt Crc16 osztály crc függvényével
         generált azonosítóért ez a DHTNode felel),
        akkor a szerver eltárolja a fájl nevét és a hozzá tartozó tartalmat.

        Ha nem ez a szerver felelős a fájlért,
        akkor felveszi a kapcsolatot a soron következő szerverrel
        (ennek meghatározásának módját lásd lent),
        és egy "upload" kéréssel áttölti a tartalmat oda.

        A fájlt az érte felelős szerver valamilyen alkalmas adatszerkezetben
        tárolja a memóriában.

    lookup <fájlnév>

        A fájl tartalmának lekérése.
        A válasz első sora lehet "found",
        ebben az esetben a válasz a fájl soraival folytatódik,
        illetve a válasz lehet "not-found",
        ami azt jelenti, hogy a rendszerben nem található meg
        a keresett nevű fájl.

        Ha a fájlért ez a szerver a felelős,
        akkor a válasz rögtön megadható,
        különben a soron következő szervertől kell lekérni egy "lookup" művelettel.

Feltételezhető, hogy a rendszerből nem lépnek ki szerverek.


A mutatótábla (finger table) meghatározása
------------------------------------------

Tegyük fel, hogy a DHTNode-ok azonosítói sorban a_1, a_2, ..., a_n.
Ekkor az i-edik DHTNode az (a_(i-1)+1..a_i) azonosítójú fájlokért lesz felelős, vagyis

    a_1    az     a_n    +1 .. a_1   azonosítójú fájlokért felelős,
    a_2    az     a_1    +1 .. a_2   azonosítójú fájlokért felelős,
    ...                     .. 
    a_n    az     a_(n-1)+1 .. a_n   azonosítójú fájlokért felelős.

Emlékeztető: minden érték modulo 2^16 értendő,
tehát a 65534..1 intervallum értékei 65534, 65535, 0 és 1.

Nevezzük az i azonosító rákövetkezőjének azt a DHTNode azonosítót,
amelyik az i-nél nagyobb vagy egyenlő azonosítók közül a legkisebb.
Ez azt jelenti, hogy ha egy fájl azonosítója i,
akkor érte felelős DHTNode azonosítója pont az i rákövetkezője.


Ha a DHTNode azonosítója s, akkor a táblázatának n-edik (n=1..16) eleme
az ((s+2^(n-1)) mod 2^16) azonosító rákövetkezőjét tartalmazza.
Ennek az az intuitív jelentése, hogy melyik azonosítójú szerver felelős
a (s+2^(n-1)+1 .. s+2^n) azonosítójú fájlokért.

A táblázatot a DHTMain határozza meg mindegyik DHTNode-hoz akkor,
amikor már mindegyik DHTNode azonosítója ismert.


A soron következő szerver meghatározása
---------------------------------------

A DHTNode sorban elkezdi vizsgálni a mutatótáblájának tartalmát,
és azt a szervert választja ki, amelyik a táblázat szerint
a keresett azonosítóért felelőshöz a legközelebb esik.
Ha a DHTNode önmaga felelős a fájlért, akkor természetesen nem kell keresnie,
egyébként a mutatótábla elemeit kell megvizsgálnunk;
az i-edik elemet (i=1..16) jelölje ft[i].

Ha a keresett elem azonosítója a DHTNode azonosítója és ft[1] közötti értékű,
akkor biztosan a következő (ft[1]-hez tartozó) szerver felelős a fájlért,
ezért ezt választjuk eredménynek.

Ha található olyan i index, amelyre teljesül, hogy

    ft[i] <= keresett azonosító < ft[i+1],

akkor ft[i]-t adja a keresés eredményként.

Ha a keresett azonosító nagyobb a mutatótábla utolsó bejegyzésénél is,
akkor a keresés eredménye az utolsó bejegyzéshez tartozó szerver.


Példa
-----

A DHTabra.pdf szemlélteti a feltöltés és a keresés során a mutatótábla használatát.
