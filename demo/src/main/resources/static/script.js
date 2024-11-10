function toggleScore() {
    const scoreSection = document.getElementById('scoreSection');
    scoreSection.style.display = (scoreSection.style.display === 'none' || scoreSection.style.display === '') ? 'block' : 'none';
}

function toggleAnswers() {
    const answersSection = document.getElementById('answersSection');
    answersSection.style.display = (answersSection.style.display === 'none' || answersSection.style.display === '') ? 'block' : 'none';
}

function confirmDeletion() {
    return confirm('Are you sure you want to delete your account? This action cannot be undone.');
}