const apiVersions: Record<string, string> = {
  v1: 'https://prod.sharedsystemshome.com/api/v1',
  v2: 'https://prod.sharedsystemshome.com/api/v2'
};

export const environment = {
  production: false,
  apiVersions,
  defaultApiVersion: 'v1',
  alerting: 'material'
};
