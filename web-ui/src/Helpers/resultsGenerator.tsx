export interface ResultCard {
  url: string;
  title: string;
  lastUpdated: number;
  summary?: string;
  tags?: Array<string>;
  source: string;
  logoKey: string;
}

export function generateResults(quantity: number) {
  var results: Array<ResultCard> = [];

  for (let i = 0; i < quantity; i++) {
    var newCard: ResultCard = {
      url: `http://${generateRandomString(20, false)}`,
      title: "No results :(", //average headline length
      lastUpdated: new Date(2021, 11, 31, 12).getTime() / 1000,
      summary: generateRandomString(200),
      tags: Array.from({ length: randomIntFromInterval(1, 6) }, () =>
        generateRandomString(10, false)
      ),
      source: "Fake News Media",
      logoKey: "news-source-logos/diariodecuba.png",
    };

    results.push(newCard);
  }

  return results;
}

function generateRandomString(size: number, withSpaces: boolean = true) {
  var characters =
    "ABCD EFGH IJKL MNOP QRST UVWX YZab cdef ghij klmn opqr stuv wxyz 0123 4567 89";

  if (!withSpaces) {
    characters = characters.replace(/\s/g, "");
  }

  let res = "";
  const charactersLength = characters.length;
  for (let i = 0; i < size; i++) {
    res += characters.charAt(Math.floor(Math.random() * charactersLength));
  }

  return res;
}

function randomIntFromInterval(min: number, max: number) {
  // min and max included
  return Math.floor(Math.random() * (max - min + 1) + min);
}
