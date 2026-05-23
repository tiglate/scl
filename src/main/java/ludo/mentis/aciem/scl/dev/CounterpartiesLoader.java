package ludo.mentis.aciem.scl.dev;

import ludo.mentis.aciem.scl.domain.Counterparty;
import ludo.mentis.aciem.scl.domain.Document;
import ludo.mentis.aciem.scl.repos.CounterpartyRepository;
import ludo.mentis.aciem.scl.repos.DocumentTypeRepository;
import ludo.mentis.aciem.scl.util.RandomUtils;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;

@Component
public class CounterpartiesLoader implements DataLoaderCommand {

	private final RandomUtils randomUtils;
	private final CounterpartyRepository counterpartyRepository;
	private final DocumentTypeRepository documentTypeRepository;

	public CounterpartiesLoader(final RandomUtils randomUtils,
			final CounterpartyRepository counterpartyRepository,
			final DocumentTypeRepository documentTypeRepository) {
		this.randomUtils = randomUtils;
		this.counterpartyRepository = counterpartyRepository;
		this.documentTypeRepository = documentTypeRepository;
	}

	@Override
	public int getOrder() {
		return 1;
	}

	@Override
	public String getName() {
		return "Counterparties";
	}

	@Override
	public boolean canItRun() {
		return counterpartyRepository.count() == 0;
	}

	@Override
	public int run() {
		var count = 0;
		var faker = new Faker();

		for (; count < 100; count++) {
			var randomInt = faker.random().nextInt(100, 10000);
			var counterparty = new Counterparty();
			var longName = faker.company().name();
			var shortName = longName.contains(" ") ? longName.split(" ")[0].replace(",", "") : null;
			if (shortName == null || shortName.isEmpty()) {
				shortName = longName.contains("-") ? longName.split("-")[0].replace(",", "") : null;
			}
			counterparty.setOriginId(faker.random().nextInt(1000, 10000));
			counterparty.setLongName(longName);
			counterparty.setShortName(shortName);
			counterparty.setIsActive(randomUtils.pickRandomBoolean());
			counterparty.setDocuments(new HashSet<>());
			var doc = new Document();
			doc.setCounterparty(counterparty);
			if (randomInt % 5 == 0) {
				doc.setDocumentType(documentTypeRepository.findByNameIgnoreCase("CPF").orElseThrow());
				doc.setValue(faker.cpf().valid());
			} else if (randomInt % 2 == 0) {
				doc.setDocumentType(documentTypeRepository.findByNameIgnoreCase("CNPJ").orElseThrow());
				doc.setValue(faker.cnpj().valid());
			} else {
				doc.setDocumentType(documentTypeRepository.findByNameIgnoreCase("EIN").orElseThrow());
				doc.setValue(faker.number().digits(2) + "-" + faker.number().digits(7));
			}
			if (randomInt % 13 == 0) {
				doc.setExpiration(randomUtils.getRandomDate(LocalDate.now().minusMonths(6), LocalDate.now()));
			}
			counterparty.getDocuments().add(doc);
			counterpartyRepository.save(counterparty);
		}

		return count;
	}

}
