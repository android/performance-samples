import {IS_LOG} from './flags';

const TAG = "MacroBenchmarks"

export function log(message: string, ...args: any[]) {
  if (IS_LOG) {
    let length = args ? args.length : 0;
    if (length > 0) {
      console.log(TAG, message, ...args);
    } else {
      console.log(TAG, message);
    }
  }
};
