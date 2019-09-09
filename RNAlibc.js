import {NativeModules} from 'react-native';
const {RNAlibcSdk} = NativeModules;

const initTae = () =>
  new Promise((success, error) => {
    RNAlibcSdk.initTae(success, error);
  });

const showLogin = () =>
  new Promise((success, error) => {
    RNAlibcSdk.showLogin(success, error);
  });

const showLogout = () =>
  new Promise((success, error) => {
    RNAlibcSdk.showLogout(success, error);
  });

const getUserInfo = () =>
  new Promise((success, error) => {
    RNAlibcSdk.getUserInfo(success, error);
  });

const isLogin = () =>
  new Promise(success => {
    RNAlibcSdk.isLogin(success);
  });

const setChannel = RNAlibcSdk.setChannel;

const setISVVersion = RNAlibcSdk.setISVVersion;

const setSyncForTaoke = isSyncForTaoke =>
  new Promise((success, error) => {
    RNAlibcSdk.setSyncForTaoke(isSyncForTaoke, success, error);
  });

const show = data =>
  new Promise((success, error) => {
    RNAlibcSdk.show(data, success, error);
  });

export default {
  initTae,
  showLogin,
  showLogout,
  getUserInfo,
  isLogin,
  setChannel,
  setISVVersion,
  setSyncForTaoke,
  show,
};
