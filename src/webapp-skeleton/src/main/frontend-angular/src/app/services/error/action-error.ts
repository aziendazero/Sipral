export class ActionError extends Error {

  entity;

  constructor(message, entity) {
    super();
    this.name = this.constructor.name;
    this.message = message;
    // if (typeof Error.captureStackTrace === 'function') {
    //   Error.captureStackTrace(this, this.constructor);
    // } else {
      this.stack = (new Error(message)).stack;
    // }
    this.entity = entity;
  }
}
