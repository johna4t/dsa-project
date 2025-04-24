declare module 'alertifyjs' {
  const alertify: AlertifyJS.AlertifyStatic;
  export = alertify;
}

declare namespace AlertifyJS {
  interface AlertifyStatic {
    alert(message: string, onok?: () => void): AlertifyStatic;
    confirm(message: string, onok: (event: any) => void, oncancel?: () => void): AlertifyStatic;
    success(message: string): AlertifyStatic;
    error(message: string): AlertifyStatic;
    warning(message: string): AlertifyStatic;
    message(message: string): AlertifyStatic;
    dismissAll(): void;
    // Add other methods you plan to use here
  }
}
