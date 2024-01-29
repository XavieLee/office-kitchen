
function isValidUsername (str) {
  return ['admin', 'editor'].indexOf(str.trim()) >= 0;
}

function isExternal (path) {
  return /^(https?:|mailto:|tel:)/.test(path);
}

//判断是不是邮箱
function isEmail(str) {
  const string = str.replace(/\s|&nbsp;/g, ''); //先去除用户输入的无效字符
  const reg = /^[a-zA-Z0-9_-]+@([a-zA-Z0-9]+\.)+(com|cn|net|org)$/;
  if (reg.test(string)) {
    return true;
  } else {
    return false;
  }
}

function isCellPhone (val) {
  if (!/^1(3|4|5|6|7|8)\d{9}$/.test(val)) {
    return false
  } else {
    return true
  }
}

//校验账号
function checkUserName (rule, value, callback){
  if (value == "") {
    callback(new Error("请输入账号"))
  } else if (value.length > 20 || value.length <3) {
    callback(new Error("账号长度应是3-20"))
  } else {
    callback()
  }
}

//校验姓名
function checkName (rule, value, callback){
  if (value == "") {
    callback(new Error("请输入姓名"))
  } else if (value.length > 12) {
    callback(new Error("账号长度应是1-12"))
  } else {
    callback()
  }
}

function checkPhone (rule, value, callback){
  // let phoneReg = /(^1[3|4|5|6|7|8|9]\d{9}$)|(^09\d{8}$)/;
  if (value == "") {
    callback(new Error("请输入邮箱"))
  } else if (!isEmail(value)) {//引入methods中封装的检查手机格式的方法
    callback(new Error("请输入正确的邮箱!"))
  } else {
    callback()
  }
}


function validID (rule,value,callback) {
  // 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
  let reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  if(value == '') {
    callback(new Error('请输入身份证号码'))
  } else if (reg.test(value)) {
    callback()
  } else {
    callback(new Error('身份证号码不正确'))
  }
}