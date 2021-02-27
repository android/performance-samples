import {IS_LOG} from './flags';

export function log(message: string, ...args: any[]) {
  if (IS_LOG) {
    let length = args ? args.length : 0;
    if (length > 0) {
      console.log(message, ...args);
    } else {
      console.log(message);
    }
  }
};
