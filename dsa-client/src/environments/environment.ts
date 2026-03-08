const apiVersions: Record<string, string> = {
  v1: '/api/v1',
  v2: '/api/v2'
};

export const environment = {
  production: false,
  apiVersions,
  defaultApiVersion: 'v1',
  alerting: 'alertify'
};
