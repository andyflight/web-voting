function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(function() {
        alert('Link copied to clipboard!');
    }).catch(function(err) {
        console.error('Could not copy text: ', err);
    });
}

document.addEventListener('DOMContentLoaded', function () {
    initCandidateManagement();
    
    initClipboardButtons();
});

function initCandidateManagement() {
    const candidatesContainer = document.getElementById('candidates-container');
    const addCandidateBtn = document.getElementById('add-candidate-btn');
    
    if (!candidatesContainer || !addCandidateBtn) {
        return;
    }
    
    addCandidateBtn.addEventListener('click', function () {
        const newCandidateField = document.createElement('div');
        newCandidateField.classList.add('candidate-field');

        const newInput = document.createElement('input');
        newInput.type = 'text';
        newInput.name = 'candidates';
        newInput.classList.add('form-control');
        newInput.placeholder = 'Candidate name';
        newInput.required = true;

        const removeBtn = document.createElement('button');
        removeBtn.type = 'button';
        removeBtn.classList.add('btn', 'btn-danger', 'btn-sm');
        removeBtn.textContent = 'Remove';
        removeBtn.addEventListener('click', function () {
            if (candidatesContainer.children.length > 1) {
                candidatesContainer.removeChild(newCandidateField);
            } else {
                alert('At least one candidate is required.');
            }
        });

        newCandidateField.appendChild(newInput);
        newCandidateField.appendChild(removeBtn);
        candidatesContainer.appendChild(newCandidateField);
    });
}

function initClipboardButtons() {
    const copyButtons = document.querySelectorAll('.copy-link-btn');
    copyButtons.forEach(button => {
        button.addEventListener('click', function() {
            const textToCopy = this.getAttribute('data-clipboard-text');
            copyToClipboard(textToCopy);
        });
    });
}