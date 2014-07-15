/**
 * author：康乐
 * time: 2014/7/14.
 * function: 注册用户，并检查输入数据的合法性
 * */
function sign_up(){

    isUserNameLegal();
    isEmailIegal();
    isEmailUsed("email");
}

function isUserNameLegal() {

    var  nick_name;

    nick_name = document.getElementById("nick_name_value").value;

    // 用户名只能是由汉字、数字、字母、下划线组成
    var nick_Name_Pattern = /^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$/;

    var nick = nick_Name_Pattern.test(nick_name);

//    if(nick == false){
//        alert("No");
//    }else{
//        alert("YES");
//    }
}



//这段代码用于检测用户所输入的邮箱是否符合是合法的，并在网页上给予用户提示
function isEmailIegal() {
    //下面是邮件匹配模式
    var email_Pattern = /\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/;

    var email = email_Pattern.test(document.getElementById("email_value").value);

//    if (email == false){
//        alert("Inlegal");
//    }else{
//        alert("Legal");
//    }
}

// 检测邮箱是否已经被注册
function isEmailUsed(email) {

    var request = new XMLHttpRequest();

    request.open("POST","/isEmailUsed");

    request.setRequestHeader("Content-Type","application/json;charset=utf-8");

    var email = document.getElementById("email_value").value;

    var message = '{"email":"' + email + '"}';

   // alert(message);


    request.onreadystatechange = function() {
        if (request.readyState === 4 && request.status === 200){

            var type = request.getResponseHeader("Content-Type");


          //  alert("?");

            // 如果返回的是json数据类型，进行相应的解析处理
            if (type === "application/json"){

                var responseMessage = request.responseText;

                alert(responseMessage);


            }




        }
    }

    request.send(message);
}






