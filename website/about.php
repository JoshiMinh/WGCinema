<?php
$pageTitle = "WG Cinema - About Us";
include 'ui/header.php';
?>

<div class="container my-5 py-5">
    <div class="glass-panel p-5 mt-5">
        <h1 class="display-4 fw-bold gradient-text mb-4">About WG Cinema</h1>
        <p class="text-muted-custom fs-5 mb-4">
            WG Cinema is a state-of-the-art cinema system, bringing a superb cinematic experience at a reasonable price.
        </p>
        <div class="row g-4 mt-4">
            <?php
            $features = [
                [
                    'icon' => 'fa-star',
                    'title' => 'High Quality',
                    'desc' => 'Dolby Atmos sound system and ultra-large screens.'
                ],
                [
                    'icon' => 'fa-tags',
                    'title' => 'Affordable Prices',
                    'desc' => 'Always offering promotion packages for students and loyal customers.'
                ],
                [
                    'icon' => 'fa-heart',
                    'title' => 'Dedicated Service',
                    'desc' => 'Professional and friendly staff team.'
                ]
            ];
            foreach ($features as $feature): ?>
                <div class="col-md-4">
                    <div class="p-4 rounded-4 bg-dark bg-opacity-25 border border-secondary border-opacity-25 h-100 text-center">
                        <i class="fas <?= $feature['icon'] ?> fs-1 gradient-text mb-3"></i>
                        <h4 class="text-white"><?= $feature['title'] ?></h4>
                        <p class="text-muted-custom small mb-0"><?= $feature['desc'] ?></p>
                    </div>
                </div>
            <?php endforeach; ?>
        </div>
    </div>
</div>

<?php include 'ui/footer.php'; ?>