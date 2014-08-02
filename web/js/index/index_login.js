/**
 * Created by leon on 2014/7/19.
 */

function isEmailLegal() {

	var email_Pattern = /\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/;      // 邮件匹配正则表达式
	var email = document.getElementById("user_email_value").value;
	var isLegal = email_Pattern.test(email);

	if (isLegal == false) {

		/**
		 * 如果格式存在问题，那么就提示用户格式存在问题，并动态的插入一个p元素
		 */
		if (document.getElementById("email_format_check") == null) {
			var element = document.createElement("p");
			element.setAttribute("id", "email_format_check");
			element.setAttribute("style", "position: absolute;font-size:12px;color:red;left:80px;top:125px");
			var message = "邮箱格式有误";
			var text = document.createTextNode(message);
			element.appendChild(text);
			document.getElementById("user_email").appendChild(element);
		}

		return false;
	} else {

//        var legal_message = "邮箱格式正确";

		if (document.getElementById("email_format_check") == null) {

			//邮箱格式正，暂时是--没有消息，就是好消息

		} else {
			document.getElementById("email_format_check").remove();
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

	var message = '{"email":"' + email + '"}';


	request.onreadystatechange = function () {
		if (request.readyState === 4 && request.status === 200) {

			var type = request.getResponseHeader("Content-Type");


			// 如果返回的是json数据类型，进行相应的解析处理
			if (type === "application/json") {

				var responseMessage = request.responseText;

				var jsonObject = JSON.parse(responseMessage);

				if (jsonObject.isUsed == "yes") {

					if (document.getElementById("email_isUsed_check") == null) {
						//do nothing
					} else {

						document.getElementById("email_isUsed_check").remove();

					}

					return true;

				} else {

					var element = document.createElement("p");
					element.setAttribute("id", "email_format_check");
					element.setAttribute("style", "position: absolute;font-size:12px;color:red;left:80px;top:125px");
					var message = "邮箱不存在";
					var text = document.createTextNode(message);
					element.appendChild(text);
					document.getElementById("user_email").appendChild(element);
				}
				return false;
			}
		}
	}

	request.send(message);
}


function isPasswordLegal() {
	var pas = document.getElementById("user_ps_value").value;
	if (pas.length < 6) {
		if (document.getElementById("password_format_check") == null) {
			var element = document.createElement("p");
			element.setAttribute("id", "password_format_check");
			element.setAttribute("style", "position: absolute;font-size:12px;color:red;left:80px;top:180px");
			var message = "密码少于6位";
			var text = document.createTextNode(message);
			element.appendChild(text);
			document.getElementById("user_password").appendChild(element);
		}
		return false;
	} else {
		if (document.getElementById("password_format_check") != null) {
			document.getElementById("password_format_check").remove();
		} else {
			// do nothing
		}

		return true;
	}
}




