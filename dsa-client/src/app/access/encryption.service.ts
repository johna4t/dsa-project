import { Injectable } from '@angular/core';
import * as CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root'
})
export class EncryptionService {

  // Encryption Key (you should keep this secret)
  private key: string = 'your-secret-key';

  constructor() { }


}
