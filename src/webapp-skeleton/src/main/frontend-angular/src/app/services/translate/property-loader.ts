import { TranslateLoader } from '@ngx-translate/core';
import { fromPromise } from 'rxjs/observable/fromPromise';

export class ProperyLoader implements TranslateLoader {

  constructor(private apiUrl) { }

  /**
   * Gets the translations from the server
   * @param lang
   * @returns {any}
   */
  public getTranslation(lang: string): any {

    return fromPromise(fetch(this.apiUrl + `messages_${lang}.properties?_=${new Date().getTime()}`, { method: "GET", credentials: 'include'})
      .then(response => response.text())
      .then(properties => {
        const keyValuePairs: Array<string> = properties.split('\n');
        const translation = {};

        for (let keyValuePair of keyValuePairs) {
          if (keyValuePair.indexOf('=') !== -1) {
            const keyValue = keyValuePair.trim().split('=');
            if (keyValue[1]) {
              translation[keyValue[0]] = this.decode(keyValue[1]);
            } else {
              console.error(`Translation for lang ${lang} of key ${keyValue[0]} is undefined!`);
            }
          }
        }
        return translation;
      })
      .catch(error => {
        console.error(`Error getting translation for lang ${lang} ${error.message}`, error);
        throw error;
      }));
  }

  private decode(prop): string {
    let rawText = prop.replace(/\\u([0-9A-F]{4})/g, (whole, group1) => String.fromCharCode(parseInt(group1, 16)));
    rawText = rawText.replace('\\!', '!');
    rawText = rawText.replace('\\:', ':');
    return rawText;
  }

}
