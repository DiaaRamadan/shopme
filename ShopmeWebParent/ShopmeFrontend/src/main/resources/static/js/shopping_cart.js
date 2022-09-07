$(document).ready(function() {

	$(".linkMinus").click(function(evt) {
		evt.preventDefault($(this));
		decreaseQuantity($(this))
	});


	$(".linkPlus").click(function(evt) {
		evt.preventDefault($(this));
		increaseQuantity($(this));
	});

});

function decreaseQuantity(link) {
	const productId = link.attr("pid");
	const quantityInput = $("#quantity" + productId);
	const newQuantity = parseInt(quantityInput.val()) - 1;

	if (newQuantity > 0) {
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity);
	} else {
		alert("Minimum quantity is 1");
	}
}

function increaseQuantity(link) {
	const productId = link.attr("pid");
	const quantityInput = $("#quantity" + productId);
	const newQuantity = parseInt(quantityInput.val()) + 1;
	if (newQuantity <= 5) {
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity);
	} else {
		alert("Maximun quantity is 5");
	}
}

function updateQuantity(productId, quantity){
	const url = contextPath + "cart/update/" + productId + "/" + quantity;
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(newSubTotal) {
		updateSubTotal(newSubTotal, productId);
		updateTotal();
	}).fail(function(reposne) {
		alert("Error while updating product to cart");
	});
	
}

function updateSubTotal(updatedSubTotal, productId){
	$("#subtotal" + productId).text(updatedSubTotal);
}

function updateTotal(){
	let total = 0.0;
	
	$(".subtotal").each(function(index, element){
		
		total += parseFloat($(element).text().replace(",", ""));
		
	});
	
	$("#total").text(total);
}

