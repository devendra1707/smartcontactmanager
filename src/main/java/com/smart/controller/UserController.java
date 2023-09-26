package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Helper.Message;
import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private MyOrderRepository myOrderRepository;

	// method for getting common data response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();
		System.out.println("USERNAME" + userName);
		User user = userRepository.getUserbyUserName(userName);
		System.out.println("USER" + user);
		model.addAttribute("User", user);

	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User-Dashboard");

		return "normal/user_dashboard";

	}

	// open ad d contact handler

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add-contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

	// process add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserbyUserName(name);

			// Processing and uploading file
			if (file.isEmpty()) {
				// if file is empty then try your message
				System.out.println("file is empty");
				// if contact file img file is empty we pass this msg
				contact.setImage("contact.jpg");
			} else {

				// file the file to folder and update the conctact
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is Uploaded");

			}
			contact.setUser(user);

			user.getContacts().add(contact);
			this.userRepository.save(user);

			// print contact data from user
			System.out.println("DATA " + contact);
			System.out.println("Added To Database");
			// message success ......... to database
			session.setAttribute("message", new Message("Your Contact is added !! ADD more ....", "success"));
		} catch (Exception e) {

			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			/// error message to send
			session.setAttribute("message", new Message("Something Went wrong !!", "ISSUE"));
		}
		return "normal/add_contact_form";
	}

// its in normal folder 

	// from per page how much page you want to show
	// per page= 5{n} n number contact per page
	// current page = 0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Show User Contacts");

		// title s contact list send here
		// will come from database to show in view contacts
		String userName = principal.getName();
		User user = this.userRepository.getUserbyUserName(userName);
		// you are here getting full list of contacts or u can use user repository

		/* List<Contact>contacts = user.getContacts(); */
		// will create a repository of contacts
		// now with help of contact repository we can find contact who have logined in

		// will implment a custom method in contact repository
		// pageable is parent interface so we can store page request object in pageable
		Pageable pageable = PageRequest.of(page, 8);

		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}

	// showing particular contacts details;
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal princpal) {
		System.out.println("CID " + cId);
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		String userName = princpal.getName();
		User user = this.userRepository.getUserbyUserName(userName);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		return "normal/contact_detail";
	}

	// delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,
			Principal principal) {
		// Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = this.contactRepository.findById(cId).get();
		// Contact contact = contactOptional.get();
		// chsnged delteing the contact from db
		System.out.println("contact" + contact.getcId());
		User user = this.userRepository.getUserbyUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);

		// for finding current user

		System.out.println("DELETED");
		session.setAttribute("message", new Message("Contact is deleted succesfully", "success"));
		return "redirect:/user/show-contacts/0";
	}

	// open update form handler
	// getmapping can be opened by any url if not resolved
	// you have to use if condition if u are using get mapping method
	// by post method we can secure the url
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model m) {
		m.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(cid).get();
		m.addAttribute(contact);
		return "normal/update_form";

	}

	// update contact handler
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, HttpSession session, Principal principal) {
		// check if person hasa selected new img we gonna rewrite or is it empty
		try {
			// old photo del fetch
			Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();
			if (!file.isEmpty()) {
				// file work
				// working on img by del old img and bringing new latest one
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldcontactDetail.getImage());
				file1.delete();
				// update new photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldcontactDetail.getImage());
			}

			User user = this.userRepository.getUserbyUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("messaage", new Message("Your Contact had been updated .....", "Success"));

		} catch (Exception e) {
			e.setStackTrace(null);
			// TODO: handle exception
		}
		System.out.println("Contact Name " + contact.getName());
		System.out.println("Contact ID" + contact.getcId());

		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	// your profile handler
	// As anyone can see profile so use get mapping
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");

		return "normal/profile";
	}

	// open setting handler
	@GetMapping("/settings")
	public String openSettings() {

		return "normal/settings";
	}

	// Change password handler
	@PostMapping("/change-password")
	public String PasswordChange(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

		System.out.println("OLD PASSWORD" + oldPassword);
		System.out.println("NEW PASSWORD" + newPassword);
		// to check if old is equal to new one
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserbyUserName(userName);
		System.out.println(currentUser.getPassword());

		if (this.bcryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			// change the password
			// bcrypt password will encode old password into new password

			currentUser.setPassword(this.bcryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your password is sucessfully changed ....", "success"));
		} else {
			// error...

			session.setAttribute("message", new Message(" Your old Password is incorrect....", "danger"));
			return "redirect:/user/settings";
		}

		return "redirect:/user/index";
	}

	// creating order for payment
	/*
	 * @PostMapping("/create_order")
	 * 
	 * @ResponseBody public String createOrder(@RequestBody Map<String, Object>
	 * data, Principal principal) throws RazorpayException { //
	 * System.out.println("Hey order function executed"); System.out.println(data);
	 * int amt = Integer.parseInt(data.get("amount").toString()); // in java 11 we
	 * can use var in the place of creating an object int client = new
	 * RazorpayClient("rzp_test_cNZhxink7mzn6o", "QegM0dwRF34sRv4c37qdXGhy");
	 * 
	 * JSONObject ob = new JSONObject(); ob.put("amount", amt*100); // converting
	 * into rupees that why we multipy with 100 ob.put("currency", "INR");
	 * ob.put("receipt", "txn_235425"); //creating order new and getting it Order
	 * order = client.orders.create(ob); System.out.println(order); // save the
	 * order in database; MyOrder myOrder = new MyOrder();
	 * myOrder.setAmount(order.get("amount")+ "");
	 * myOrder.setOrderId(order.get("id")); myOrder.setPayementId(null);
	 * myOrder.setStatus("created");
	 * myOrder.setUser(this.userRepository.getUserbyUserName(principal.getName()));
	 * myOrder.setReceipt(order.get("receipt"));
	 * this.myOrderRepository.save(myOrder);
	 * 
	 * 
	 * // if you want you can save this to your data...........
	 * 
	 * 
	 * return order.toString(); }
	 */
	@PostMapping("/update_order")
	public ResponseEntity<?>updateOrder(@RequestBody Map<String , Object> data){
	MyOrder myorder	= this.myOrderRepository.findByOrderId(data.get("order_id").toString());
		myorder.setPayementId(data.get("payment_id").toString());
		myorder.setStatus(data.get("status").toString());
		this.myOrderRepository.save(myorder);
		
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg","updated"));
	}
}
