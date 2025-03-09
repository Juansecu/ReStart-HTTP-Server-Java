const resendVerificationEmailButton = document.getElementById("resendVerificationEmailButton");

resendVerificationEmailButton.addEventListener("click", resendVerificationEmail);

async function resendVerificationEmail() {
    /**
     * @type {HTMLElement}
     */
    let alertIcon = null;
    /**
     * @type {{
     *     errorCode: 'TOO_MANY_ATTEMPTS' | 'USER_ALREADY_VERIFIED' | 'USER_NOT_FOUND',
     *     message: string,
     *     successful: boolean
     * }}
     */
    let data = null;
    /**
     * @type {Response}
     */
    let response = null;

    const alert = document.getElementById("alert");
    const alertMessage = document.getElementById("alertMessage");
    const alertTitle = document.getElementById("alertTitle");
    const userId = parseInt(document.getElementById("userId").value);

    resendVerificationEmailButton.setAttribute('disabled', 'disabled');

    try {
        response = await fetch("/api/auth/resend-verification-email", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ userId })
        });
        data = await response.json();
    } catch (error) {
        resendVerificationEmailButton.removeEventListener(
            'click',
            resendVerificationEmail
        );

        resendVerificationEmailButton.remove();

        alertMessage.innerText = "There was an unexpected error while sending the verification email. Please try again later.";
        alertTitle.innerText = "Error Sending Email!";

        return;
    }

    if (response.ok) {
        resendVerificationEmailButton.removeEventListener(
            'click',
            resendVerificationEmail
        );

        resendVerificationEmailButton.remove();

        alert.classList.remove("alert--error");
        alert.classList.add('alert--success');

        alertIcon = document.getElementById("alertIcon");

        alertIcon.setAttribute('viewBox', '0 0 36 36');

        alertIcon.innerHTML = `<path d="M18 2a16 16 0 1 0 16 16A16 16 0 0 0 18 2Zm0 30a14 14 0 1 1 14-14a14 14 0 0 1-14 14Z" class="clr-i-outline clr-i-outline-path-1"/>
            <path d="M28 12.1a1 1 0 0 0-1.41 0l-11.1 11.05l-6-6A1 1 0 0 0 8 18.53L15.49 26L28 13.52a1 1 0 0 0 0-1.42Z" class="clr-i-outline clr-i-outline-path-2"/>
            <path fill="none" d="M0 0h36v36H0z"/>`;

        alertMessage.innerText = "An email has been sent to your email address. Please check your inbox and follow the instructions to verify your account.";
        alertTitle.innerText = "Verification Email Sent!";

        return;
    }

    resendVerificationEmailButton.removeEventListener(
        'click',
        resendVerificationEmail
    );

    resendVerificationEmailButton.remove();

    switch (data.errorCode) {
        case "TOO_MANY_ATTEMPTS":
            alertMessage.innerText = "You have reached the maximum number of attempts to resend the verification email. Please try again later.";
            alertTitle.innerText = "Too Many Attempts!";

            break;
        case "USER_ALREADY_VERIFIED":
            alertMessage.innerText = "Your account has already been verified. Please login to continue.";
            alertTitle.innerText = "Account Already Verified!";

            break;
        case "USER_NOT_FOUND":
            alertMessage.innerText = "We couldn't find your account. Please try again later.";
            alertTitle.innerText = "Account Not Found!";

            break;
        default:
            alertMessage.innerText = "There was an unexpected error while sending the verification email. Please try again later.";
            alertTitle.innerText = "Error Sending Email!";
    }
}
