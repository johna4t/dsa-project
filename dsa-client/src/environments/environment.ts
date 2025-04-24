const apiVersions: Record<string, string> = {
  v1: 'http://localhost:8080/api/v1',
  v2: 'http://localhost:8080/api/v2'
};

export const environment = {
  production: false,
  apiVersions,
  defaultApiVersion: 'v1',
  alerting: 'alertify'
};
