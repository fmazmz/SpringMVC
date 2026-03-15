document.addEventListener('DOMContentLoaded', function() {
    const filterForm = document.querySelector('.filter-form') || document.getElementById('filterForm');
    if (!filterForm) return;

    const textInputs = filterForm.querySelectorAll('input[type="text"]');
    const selects = filterForm.querySelectorAll('select:not([name="sort"]):not([name="size"])');

    let timeout;
    textInputs.forEach(input => {
        input.addEventListener('input', () => {
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                filterForm.submit();
            }, 500);
        });
    });

    selects.forEach(select => {
        select.addEventListener('change', () => {
            filterForm.submit();
        });
    });
});

