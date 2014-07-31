/**
 * author: 康乐
 * time: 2014/7/15
 * function: 检测用户输入数据（包括昵称、用户名、邮箱和密码）的合法性和使用Ajax检测邮箱
 * 是否已经被注册
 * */

function isUserNameLegal() {

    var nick_name;

    nick_name = document.getElementById("nick_name_value").value;

    // 用户名只能是由汉字、数字、字母、下划线组成
    var nick_Name_Pattern = /^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$/;

    var isLegal = nick_Name_Pattern.test(nick_name);

    if (isLegal == false) {
        if (document.getElementById("nick_name_format_check") == null) {
            var element = document.createElement("p");
            element.setAttribute("id", "nick_name_format_check");
            element.setAttribute("style", "font-size:12px;color:red;margin-top:2px");
            var message = "用户名只能是中文、字母、数字、下划线组合";
            document.getElementById("email_input").setAttribute("style", "margin-top:13px");
            var text = document.createTextNode(message);
            element.appendChild(text);
            document.getElementById("nick_name").appendChild(element);
        } else {

        }

        return false;
    } else {
        if (document.getElementById("nick_name_format_check") == null) {
            //暂时不处理格式合法的要求
        } else {
            document.getElementById("nick_name_format_check").remove();
            document.getElementById("email_input").setAttribute("style", "margin-top:20px");
        }

        return true;

    }

}


//这段代码用于检测用户所输入的邮箱是否符合是合法的，并在网页上给予用户提示
function isEmailLegal() {

    var email_Pattern = /\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/;      // 邮件匹配正则表达式
    var email = document.getElementById("email_value").value;
    var isLegal = email_Pattern.test(email);

    if (isLegal == false) {

        /**
         * 如果格式存在问题，那么就提示用户格式存在问题，并动态的插入一个p元素
         */
        if (document.getElementById("email_format_check") == null) {
            var element = document.createElement("p");
            element.setAttribute("id", "email_format_check");
            element.setAttribute("style", "font-size:12px;color:red;margin-top:2px");
            var message = "邮箱格式有误";
            document.getElementById("password").setAttribute("style", "margin-top:13px");
            var text = document.createTextNode(message);
            element.appendChild(text);
            document.getElementById("email_input").appendChild(element);
        }

        return false;
    } else {

//        var legal_message = "邮箱格式正确";

        if (document.getElementById("email_format_check") == null) {

            //邮箱格式正，暂时是--没有消息，就是好消息

//            var element = document.createElement("p");
//            element.setAttribute("id","email_format_check");
//            element.setAttribute("style","font-size:5px;color:red;margin-top:2px");
//            document.getElementById("password").setAttribute("style","margin-top:13px");
//            var text = document.createTextNode(legal_message);
//            element.appendChild(text);
//            document.getElementById("email_input").appendChild(element);

        } else {
            document.getElementById("email_format_check").remove();
            document.getElementById("password").setAttribute("style", "margin-top:20px");
        }

        //下面检测邮箱是否被注册
        isEmailUsed(email);
    }
}


// 检测邮箱是否已经被注册
function isEmailUsed(email) {

    var request = new XMLHttpRequest();

    request.open("POST", "/isEmailUsed");

    request.setRequestHeader("Content-Type", "application/json;charset=utf-8");

    //var email = document.getElementById("email_value").value;

    var message = '{"email":"' + email + '"}';


    request.onreadystatechange = function () {
        if (request.readyState === 4 && request.status === 200) {

            var type = request.getResponseHeader("Content-Type");


            // 如果返回的是json数据类型，进行相应的解析处理
            if (type === "application/json") {

                var responseMessage = request.responseText;

	            var jsonObject = JSON.parse(responseMessage);

                if (jsonObject.isUsed == "no") {

                    if (document.getElementById("email_isUsed_check") == null) {
                        //do nothing
                    } else {

                        document.getElementById("email_isUsed_check").remove();
                        document.getElementById("password").setAttribute("style", "margin-top:20px");

                    }

                    return true;

                } else {

                    var element = document.createElement("p");
                    element.setAttribute("id", "email_isUsed_check");
                    element.setAttribute("style", "font-size:12px;color:red;margin-top:2px");
                    var message = "邮箱已经被注册";
                    document.getElementById("password").setAttribute("style", "margin-top:13px");
                    var text = document.createTextNode(message);
                    element.appendChild(text);
                    document.getElementById("email_input").appendChild(element);
                }

                return false;

            }
        }
    }

    request.send(message);
}


function isPasswordLegal() {
    var pas = document.getElementById("password_value").value;
    if (pas.length < 6) {
        if (document.getElementById("password_format_check") == null) {
            var element = document.createElement("p");
            element.setAttribute("id", "password_format_check");
            element.setAttribute("style", "font-size:12px;color:red;margin-top:2px");
            var message = "密码长度需大于6位";
            document.getElementById("password_again").setAttribute("style", "margin-top:13px");
            var text = document.createTextNode(message);
            element.appendChild(text);
            document.getElementById("password").appendChild(element);
        }
        return false;
    } else {
        if (document.getElementById("password_format_check") != null) {
            document.getElementById("password_format_check").remove();
            document.getElementById("password_again").setAttribute("style", "margin-top:20px");
        } else {
            // do nothing
        }

        return true;
    }
}


function isPasswordSame() {

    var password = document.getElementById("password_value").value;
    var password_again = document.getElementById("password_value_again").value;

    if (password == password_again) {
        if (document.getElementById("password_again_check")) {
            document.getElementById("password_again_check").remove();
            document.getElementById("sign").setAttribute("style", "margin-top:10px");
        }

        return true;
    } else {
        if (document.getElementById("password_again_check") == null){
            var element = document.createElement("p");
            element.setAttribute("id", "password_again_check");
            element.setAttribute("style", "font-size:12px;color:red;margin-top:2px");
            var message = "两次输入密码不一致";
            document.getElementById("sign").setAttribute("style", "margin-top:5px");
            var text = document.createTextNode(message);
            element.appendChild(text);
            document.getElementById("password_again").appendChild(element);
        }

        return false;
    }
}

