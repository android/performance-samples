import * as F from "firebase-functions";

import { firebaseTestLabHandler, firebaseTestLabHttpsHandler } from './handlers';

// Register handlers
export const completionHandler = F.testLab.testMatrix().onComplete(firebaseTestLabHandler);
export const httpsHandler = F.https.onRequest(firebaseTestLabHttpsHandler);
