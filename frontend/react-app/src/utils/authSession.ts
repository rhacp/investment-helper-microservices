export const AUTH_TOKEN_KEY = 'investment_helper_token';
export const SESSION_EXPIRED_EVENT = 'investment_helper_session_expired';
export const SESSION_EXPIRED_MESSAGE = 'Your session has expired. Please sign in again.';

const SESSION_MESSAGE_KEY = 'investment_helper_session_message';

export function getAuthToken() {
  return localStorage.getItem(AUTH_TOKEN_KEY);
}

export function setAuthToken(token: string) {
  localStorage.setItem(AUTH_TOKEN_KEY, token);
}

export function clearAuthToken() {
  localStorage.removeItem(AUTH_TOKEN_KEY);
}

export function notifySessionExpired() {
  clearAuthToken();
  sessionStorage.setItem(SESSION_MESSAGE_KEY, SESSION_EXPIRED_MESSAGE);
  window.dispatchEvent(new Event(SESSION_EXPIRED_EVENT));
}

export function consumeSessionMessage() {
  const message = sessionStorage.getItem(SESSION_MESSAGE_KEY);
  if (message) {
    sessionStorage.removeItem(SESSION_MESSAGE_KEY);
  }
  return message;
}
