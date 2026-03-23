package ludo.mentis.aciem.scl.dev;

import ludo.mentis.aciem.scl.domain.Department;
import ludo.mentis.aciem.scl.domain.Role;
import ludo.mentis.aciem.scl.domain.User;
import ludo.mentis.aciem.scl.model.Gender;
import ludo.mentis.aciem.scl.repos.DepartmentRepository;
import ludo.mentis.aciem.scl.repos.RoleRepository;
import ludo.mentis.aciem.scl.repos.UserRepository;
import ludo.mentis.aciem.scl.util.PasswordEncoderFactory;
import ludo.mentis.aciem.scl.util.RandomUtils;
import ludo.mentis.aciem.scl.util.UserRoles;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Component
public class UsersLoader implements DataLoaderCommand {

	private final RandomUtils randomUtils;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final DepartmentRepository departmentRepository;
	private List<Department> departments;
	private List<Role> roles;
	private static final Logger log = LoggerFactory.getLogger(UsersLoader.class);

	@Value("${LDAP_USER1_PASSWORD}")
	private String user1Password;

	@Value("${LDAP_USER2_PASSWORD}")
	private String user2Password;

	public UsersLoader(final RandomUtils randomUtils,
			           final UserRepository userRepository,
			           final RoleRepository roleRepository,
					   final PasswordEncoderFactory passwordEncoderFactory,
			           final DepartmentRepository departmentRepository) {
		this.randomUtils = randomUtils;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoderFactory.create();
		this.departmentRepository = departmentRepository;
	}
	
	@Override
	public int getOrder() {
		return 0;
	}
	
	@Override
	public String getName() {
		return "Users";
	}
	
	@Override
	public boolean canItRun() {
		return userRepository.count() == 0;
	}
	
	@Override
	public int run() {
		return createAdmin() + createRegularUsers() + createLdapUsers();
	}
	
	protected int createAdmin() {
		final String DEV_DEFAULT_PASSWORD = UUID.randomUUID().toString();
		final var user = new User();
		final var department = departmentRepository.findByNameIgnoreCase("IT").orElseThrow();
		user.setName("admin");
        user.setEmail("admin@ampliar.dev.br");
        user.setGender(Gender.MALE);
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode(DEV_DEFAULT_PASSWORD));
        user.setEnabled(true);
		user.setUseAD(false);
        user.setDepartment(department);
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
        log.info("Admin user created with password: {}", DEV_DEFAULT_PASSWORD);
        return 1;
	}
	
	protected int createRegularUsers() {
		final var departments = getDepartments();
		final var roles = getRoles();

		var count = 0;
		for (var department : departments) {
			createFakeUser(department, roles);
			createFakeUser(department, roles);
			count += 2;
		}
		return count;
	}
	
	protected void createFakeUser(Department department, List<Role> roles) {
		final var user = new User();
		final var faker = new Faker();
		user.setName(faker.name().fullName());
        user.setEmail(faker.internet().emailAddress());
        user.setGender(randomUtils.pickRandomEnumValue(Gender.class));
        user.setUsername(faker.internet().username());
        user.setPassword(passwordEncoder.encode(faker.internet().password()));
        user.setEnabled(randomUtils.pickRandomBoolean());
        user.setDepartment(department);
		user.setUseAD(false);
        user.setRoles(new HashSet<>(randomUtils.createRandomSublist(roles, 2)));
        userRepository.save(user);
	}

	protected int createLdapUsers() {
		final var faker = new Faker();
		final var departments = getDepartments();
		final var roles = getRoles();

		final var user1 = new User();
		user1.setName("LDAP User 1");
		user1.setEmail(faker.internet().emailAddress());
		user1.setGender(randomUtils.pickRandomEnumValue(Gender.class));
		user1.setUsername("user1");
		user1.setPassword(user1Password);
		user1.setEnabled(randomUtils.pickRandomBoolean());
		user1.setDepartment(departments.get(0));
		user1.setUseAD(true);
		user1.setRoles(new HashSet<>(randomUtils.createRandomSublist(roles, 2)));
		userRepository.save(user1);

		final var user2 = new User();
		user2.setName("LDAP User 2");
		user2.setEmail(faker.internet().emailAddress());
		user2.setGender(randomUtils.pickRandomEnumValue(Gender.class));
		user2.setUsername("user2");
		user2.setPassword(user2Password);
		user2.setEnabled(randomUtils.pickRandomBoolean());
		user2.setDepartment(departments.get(0));
		user2.setUseAD(true);
		user2.setRoles(new HashSet<>(randomUtils.createRandomSublist(roles, 2)));
		userRepository.save(user2);

		return 2;
	}

	protected List<Department> getDepartments() {
		return departments == null
				? departments = departmentRepository.findAll()
				: departments;
	}

	protected List<Role> getRoles() {
		return roles == null
				? roles = roleRepository.findAll()
					.stream()
					.filter(p -> !UserRoles.ADMIN.equals(p.getCode()))
					.toList()
				: roles;
	}
}
