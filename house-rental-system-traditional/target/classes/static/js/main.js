// Main JavaScript for RentEase

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Form validation enhancement
    const forms = document.querySelectorAll('.needs-validation');
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });

    // Search form enhancements
    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        // Auto-submit on filter change (with debounce)
        let searchTimeout;
        const searchInputs = searchForm.querySelectorAll('input, select');
        
        searchInputs.forEach(input => {
            input.addEventListener('input', function() {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(() => {
                    if (input.type !== 'submit') {
                        // Auto-submit search form after 500ms delay
                        // searchForm.submit();
                    }
                }, 500);
            });
        });
    }

    // Image lazy loading
    const images = document.querySelectorAll('img[data-src]');
    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.classList.remove('lazy');
                imageObserver.unobserve(img);
            }
        });
    });

    images.forEach(img => imageObserver.observe(img));

    // Booking form date validation
    const bookingForm = document.getElementById('bookingForm');
    if (bookingForm) {
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');
        
        if (startDateInput && endDateInput) {
            // Set minimum date to today
            const today = new Date().toISOString().split('T')[0];
            startDateInput.min = today;
            endDateInput.min = today;
            
            startDateInput.addEventListener('change', function() {
                endDateInput.min = this.value;
                if (endDateInput.value && endDateInput.value <= this.value) {
                    endDateInput.value = '';
                }
                calculateTotalAmount();
            });
            
            endDateInput.addEventListener('change', function() {
                calculateTotalAmount();
            });
        }
    }

    // Calculate total booking amount
    function calculateTotalAmount() {
        const startDate = document.getElementById('startDate');
        const endDate = document.getElementById('endDate');
        const pricePerMonth = document.getElementById('pricePerMonth');
        const totalAmountDisplay = document.getElementById('totalAmount');
        
        if (startDate && endDate && pricePerMonth && totalAmountDisplay) {
            if (startDate.value && endDate.value) {
                const start = new Date(startDate.value);
                const end = new Date(endDate.value);
                const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
                
                if (days > 0) {
                    const monthlyPrice = parseFloat(pricePerMonth.value);
                    const totalAmount = (monthlyPrice * days / 30).toFixed(2);
                    totalAmountDisplay.textContent = `$${totalAmount}`;
                    totalAmountDisplay.parentElement.style.display = 'block';
                } else {
                    totalAmountDisplay.parentElement.style.display = 'none';
                }
            } else {
                totalAmountDisplay.parentElement.style.display = 'none';
            }
        }
    }

    // Confirmation dialogs for delete actions
    const deleteButtons = document.querySelectorAll('[data-confirm-delete]');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            const message = this.dataset.confirmDelete || 'Are you sure you want to delete this item?';
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });

    // Loading states for forms
    const submitButtons = document.querySelectorAll('button[type="submit"]');
    submitButtons.forEach(button => {
        button.addEventListener('click', function() {
            const form = this.closest('form');
            if (form && form.checkValidity()) {
                this.disabled = true;
                this.innerHTML = '<span class="loading"></span> Processing...';
                
                // Re-enable after 10 seconds as fallback
                setTimeout(() => {
                    this.disabled = false;
                    this.innerHTML = this.dataset.originalText || 'Submit';
                }, 10000);
            }
        });
    });

    // Store original button text
    submitButtons.forEach(button => {
        button.dataset.originalText = button.innerHTML;
    });

    // Price range slider (if exists)
    const priceRange = document.getElementById('priceRange');
    if (priceRange) {
        const priceDisplay = document.getElementById('priceDisplay');
        priceRange.addEventListener('input', function() {
            if (priceDisplay) {
                priceDisplay.textContent = `$${this.value}`;
            }
        });
    }

    // Image carousel for property details
    const propertyImages = document.querySelectorAll('.property-image');
    if (propertyImages.length > 1) {
        let currentImageIndex = 0;
        
        const nextButton = document.getElementById('nextImage');
        const prevButton = document.getElementById('prevImage');
        
        if (nextButton) {
            nextButton.addEventListener('click', function() {
                currentImageIndex = (currentImageIndex + 1) % propertyImages.length;
                showImage(currentImageIndex);
            });
        }
        
        if (prevButton) {
            prevButton.addEventListener('click', function() {
                currentImageIndex = (currentImageIndex - 1 + propertyImages.length) % propertyImages.length;
                showImage(currentImageIndex);
            });
        }
        
        function showImage(index) {
            propertyImages.forEach((img, i) => {
                img.style.display = i === index ? 'block' : 'none';
            });
        }
        
        // Show first image initially
        showImage(0);
    }

    // Search suggestions (mock implementation)
    const searchInput = document.getElementById('searchKeyword');
    if (searchInput) {
        const suggestions = ['New York', 'Los Angeles', 'Chicago', 'Houston', 'Phoenix', 'Philadelphia'];
        
        searchInput.addEventListener('input', function() {
            const value = this.value.toLowerCase();
            // This would typically make an AJAX call to get real suggestions
            // For now, just a simple mock implementation
        });
    }

    // Favorite functionality (if implemented)
    const favoriteButtons = document.querySelectorAll('.favorite-btn');
    favoriteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            this.classList.toggle('favorited');
            const icon = this.querySelector('i');
            if (icon) {
                icon.classList.toggle('fas');
                icon.classList.toggle('far');
            }
        });
    });

    // Mobile menu enhancements
    const navbarToggler = document.querySelector('.navbar-toggler');
    const navbarCollapse = document.querySelector('.navbar-collapse');
    
    if (navbarToggler && navbarCollapse) {
        // Close mobile menu when clicking outside
        document.addEventListener('click', function(e) {
            if (!navbarToggler.contains(e.target) && !navbarCollapse.contains(e.target)) {
                const bsCollapse = bootstrap.Collapse.getInstance(navbarCollapse);
                if (bsCollapse && navbarCollapse.classList.contains('show')) {
                    bsCollapse.hide();
                }
            }
        });
    }

    // Animate numbers on dashboard
    const statNumbers = document.querySelectorAll('.stat-number');
    statNumbers.forEach(number => {
        const target = parseInt(number.textContent);
        let current = 0;
        const increment = target / 50;
        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                current = target;
                clearInterval(timer);
            }
            number.textContent = Math.floor(current);
        }, 20);
    });
});

// Utility functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

function formatDate(date) {
    return new Intl.DateTimeFormat('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    }).format(new Date(date));
}

// AJAX helper function
function makeRequest(url, method = 'GET', data = null) {
    return fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: data ? JSON.stringify(data) : null
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    });
}

