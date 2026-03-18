function InvalidEmail(textbox) {
    if (textbox.value === '') { 
        textbox.setCustomValidity('Bạn phải nhập địa chỉ email!'); 
    } else if (textbox.validity.typeMismatch) { 
        textbox.setCustomValidity('Địa chỉ email không đúng định dạng!'); 
    } else { 
        textbox.setCustomValidity(''); 
    } 
    return true; 
}

function InvalidUser(textbox) {
    if (textbox.value === '') { 
        textbox.setCustomValidity('Bạn phải nhập tên đăng nhập');
    } else {
        textbox.setCustomValidity(''); 
    } 
    return true; 
}

function InvalidPass(textbox) {
    if (textbox.value === '') { 
        textbox.setCustomValidity('Bạn phải nhập mật khẩu');
    } else {
        textbox.setCustomValidity(''); 
    } 
    return true;
}

function InvalidConfirmPass() {
    var password = document.getElementById("password").value;
    var confirmPassword = document.getElementById("confirm_password").value;
    var confirmPasswordField = document.getElementById("confirm_password");

    if (confirmPassword === '') {
        confirmPasswordField.setCustomValidity('Bạn phải xác nhận mật khẩu');
    } else if (confirmPassword !== password) {
        confirmPasswordField.setCustomValidity('Mật khẩu xác nhận không khớp');
    } else {
        confirmPasswordField.setCustomValidity('');
    }
    return true;
}