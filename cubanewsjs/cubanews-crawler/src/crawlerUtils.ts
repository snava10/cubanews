export enum NewsSourceName {
  ADNCUBA = "adncuba",
  CATORCE_Y_MEDIO = "catorce_y_medio",
  DIARIO_DE_CUBA = "diario_de_cuba",
  CIBERCUBA = "cibercuba",
}

export interface NewsSource {
  name: NewsSourceName;
  startUrls: Set<string>;
  datasetName: string;
}

export const newsSources = [
  {
    name: NewsSourceName.ADNCUBA,
    startUrls: new Set([
      "https://adncuba.com/",
      "https://adncuba.com/noticias-de-cuba/",
    ]),
    datasetName: "adncuba-dataset",
  },
  {
    name: NewsSourceName.CATORCE_Y_MEDIO,
    startUrls: new Set(["https://www.14ymedio.com/cuba"]),
    datasetName: "catorceymedio-dataset",
  },
  {
    name: NewsSourceName.DIARIO_DE_CUBA,
    startUrls: new Set([]),
    datasetName: "ddc-dataset",
  },
  {
    name: NewsSourceName.CIBERCUBA,
    startUrls: new Set([]),
    datasetName: "cibercuba-dataset",
  },
];

export function getNewsSourceByName(source: NewsSourceName): NewsSource {
  const res = newsSources.filter((ns) => ns.name === source).shift();
  if (!res) {
    throw new Error(`News source ${source} not found`);
  }
  return res;
}
