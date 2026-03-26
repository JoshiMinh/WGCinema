<?php
require_once 'lib/config.php';
require_once 'lib/functions.php';

$error = '';
$success = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';

    if ($action === 'login') {
        $email = $_POST['email'] ?? '';
        $password = $_POST['password'] ?? '';

        $stmt = $pdo->prepare("SELECT account_email, name, password_hash FROM accounts WHERE account_email = ?");
        $stmt->execute([$email]);
        $user = $stmt->fetch();

        if ($user && hash('sha256', $password) === $user['password_hash']) {
            if (session_status() === PHP_SESSION_NONE)
                session_start();
            $_SESSION['user_email'] = $user['account_email'];
            $_SESSION['user_name'] = $user['name'];
            header("Location: index.php");
            exit;
        }
        else {
            $error = 'Email hoặc mật khẩu không đúng.';
        }
    }
    elseif ($action === 'register') {
        $name = $_POST['name'] ?? '';
        $email = $_POST['email'] ?? '';
        $password = $_POST['password'] ?? '';
        $confirm = $_POST['confirm_password'] ?? '';

        if ($password !== $confirm) {
            $error = 'Mật khẩu xác nhận không khớp.';
        }
        else {
            $stmt = $pdo->prepare("SELECT 1 FROM accounts WHERE account_email = ?");
            $stmt->execute([$email]);
            if ($stmt->fetch()) {
                $error = 'Email này đã được đăng ký.';
            }
            else {
                $gender = $_POST['gender'] ?? 'Other';
                $dob = $_POST['dob'] ?? date('Y-m-d');
                $passwordHash = hash('sha256', $password);
                $stmt = $pdo->prepare("INSERT INTO accounts (account_email, name, password_hash, gender, date_of_birth) VALUES (?, ?, ?, ?, ?)");
                if ($stmt->execute([$email, $name, $passwordHash, $gender, $dob])) {
                    $success = 'Đăng ký thành công! Vui lòng đăng nhập.';
                }
                else {
                    $error = 'Đã có lỗi xảy ra. Vui lòng thử lại.';
                }
            }
        }
    }
}

$pageTitle = "WG Cinema - Đăng Nhập / Đăng Ký";
include 'ui/header.php';
?>

    <div class="container my-5 py-5">
        <div class="row justify-content-center mt-5">
            <div class="col-md-6 col-lg-5">
                <div class="glass-panel p-4 p-md-5 auth-page">
                    <ul class="nav nav-tabs border-0 mb-4 justify-content-center" id="authTab" role="tablist">
                        <li class="nav-item">
                            <button class="nav-link bg-transparent border-0 fs-5 fw-bold gradient-text px-4 active" id="tab-login" onclick="switchAuthTab('login')">Đăng nhập</button>
                        </li>
                        <li class="nav-item">
                            <button class="nav-link bg-transparent border-0 fs-5 fw-bold gradient-text px-4" id="tab-register" onclick="switchAuthTab('register')">Đăng ký</button>
                        </li>
                    </ul>

                    <?php if ($error): ?>
                        <div class="alert alert-danger bg-danger bg-opacity-10 border-danger text-danger mb-4" role="alert">
                            <?php echo h($error); ?>
                        </div>
                    <?php
endif; ?>

                    <?php if ($success): ?>
                        <div class="alert alert-success bg-success bg-opacity-10 border-success text-success mb-4" role="alert">
                            <?php echo h($success); ?>
                        </div>
                    <?php
endif; ?>

                    <div id="login-form">
                        <form action="auth.php" method="POST">
                            <input type="hidden" name="action" value="login">
                            <div class="mb-4">
                                <label class="form-label text-muted-custom">Email</label>
                                <input type="email" name="email" class="form-control bg-dark bg-opacity-50 border-secondary text-white py-2" placeholder="email@example.com" required>
                            </div>
                            <div class="mb-4">
                                <label class="form-label text-muted-custom">Mật khẩu</label>
                                <input type="password" name="password" class="form-control bg-dark bg-opacity-50 border-secondary text-white py-2" placeholder="••••••••" required>
                            </div>
                            <button type="submit" class="btn btn-gradient w-100 py-3 fw-bold shadow-lg">Đăng Nhập</button>
                        </form>
                    </div>

                    <div id="register-form" style="display: none;">
                        <form action="auth.php" method="POST">
                            <input type="hidden" name="action" value="register">
                            <div class="mb-4">
                                <label class="form-label text-muted-custom">Họ tên</label>
                                <input type="text" name="name" class="form-control bg-dark bg-opacity-50 border-secondary text-white py-2" placeholder="Nguyễn Văn A" required>
                            </div>
                            <div class="mb-4">
                                <label class="form-label text-muted-custom">Email</label>
                                <input type="email" name="email" class="form-control bg-dark bg-opacity-50 border-secondary text-white py-2" placeholder="email@example.com" required>
                            </div>
                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label class="form-label text-muted-custom">Giới tính</label>
                                    <select name="gender" class="form-select bg-dark bg-opacity-50 border-secondary text-white py-2" required>
                                        <option value="Male">Nam</option>
                                        <option value="Female">Nữ</option>
                                        <option value="Other">Khác</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted-custom">Ngày sinh</label>
                                    <input type="date" name="dob" class="form-control bg-dark bg-opacity-50 border-secondary text-white py-2" required>
                                </div>
                            </div>
                            <div class="mb-4">
                                <label class="form-label text-muted-custom">Mật khẩu</label>
                                <input type="password" name="password" class="form-control bg-dark bg-opacity-50 border-secondary text-white py-2" placeholder="••••••••" required>
                            </div>
                            <div class="mb-4">
                                <label class="form-label text-muted-custom">Xác nhận mật khẩu</label>
                                <input type="password" name="confirm_password" class="form-control bg-dark bg-opacity-50 border-secondary text-white py-2" placeholder="••••••••" required>
                            </div>
                            <button type="submit" class="btn btn-gradient w-100 py-3 fw-bold shadow-lg">Đăng Ký</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

<?php include 'ui/footer.php'; ?>
