# Aufgabe: Erstellung eines VNet mit LoadBalancer und VMs für eine Spring-Anwendung

## Ziel

Erstellen Sie ein virtuelles Netzwerk (VNet) in Azure, das einen LoadBalancer und zwei virtuelle Maschinen (VMs) in einem Subnetz umfasst. Auf den VMs soll eine Spring-Anwendung laufen, die spezifische Endpunkte für die Bestellverwaltung bereitstellt. Die Daten der Bestellungen sollen in einem Azure Storage Account Table gespeichert und entsprechende Benachrichtigungen sowie Zustandsänderungen über Azure Functions und Service Bus Topics gehandhabt werden.

## Anforderungen

1. **VNet und Subnetz:**

   - Erstellen Sie ein VNet in Azure mit einem Subnetz.
   - Stellen Sie sicher, dass das Subnetz für die beiden VMs und den LoadBalancer geeignet konfiguriert ist.

2. **LoadBalancer:**

   - Richten Sie einen Azure LoadBalancer ein, der Anfragen an die beiden VMs verteilt.

3. **Virtuelle Maschinen:**

   - Erstellen Sie zwei VMs innerhalb des VNets im erstellten Subnetz.
   - **Java-Installation:** Installieren Sie Java auf beiden VMs, da dies für die Ausführung der Spring-Anwendung erforderlich ist.
   - **Anwendungsbereitstellung:** Kopieren Sie die ausführbare `.jar`-Datei der Spring-Anwendung mithilfe von `scp` (Secure Copy) auf jede VM. Verwenden Sie `ssh` (Secure Shell), um sich bei den VMs anzumelden und die Anwendung zu starten. ggf. funktioniert ssh/scp nicht aus dem internen Netz, dann über den Mobilen Hotspot versuchen.
   - Konfigurieren Sie eine Spring-Anwendung auf beiden VMs, die folgende Endpunkte bereitstellt:
     - `POST /order` - Nimmt eine neue Bestellung entgegen.
     - `PUT /order/{id}` - Ändert den Zustand einer Bestellung (`akzeptiert`, `in Verarbeitung`, `in Auslieferung`, `storniert`).

4. **Azure Storage Account Table:**

   - Erstellen Sie einen Azure Storage Account und innerhalb dessen eine Table, um die Bestelldaten zu speichern.

5. **Azure Function für E-Mail-Benachrichtigungen:**

   - Implementieren Sie eine Azure Function, die getriggert wird, sobald eine neue Bestellung in der Table gespeichert wird. Diese Funktion soll dann eine E-Mail an den Benutzer senden.

6. **Azure Service Bus und Topics für stornierte Bestellungen:**
   - Erstellen Sie ein Topic im Azure Service Bus, in das eine Nachricht geschrieben wird, wenn eine Bestellung über die Spring-Anwendung storniert wird. (Tipp nutzen sie dafür die Filter Funktion)
   - Richten Sie drei Subscribers für das Topic ein:
     1. Ein Subscriber sendet eine Bestätigungsemail an den Benutzer.
     2. Ein Subscriber ändert den Zustand der Bestellung in der Azure Storage Account Table.
     3. Ein Subscriber schreibt den Zeitpunkt und Grund der Stornierung in eine separate Tabelle für spätere Auswertungen.

## Hinweise

- Verwenden Sie für die Entwicklung der Spring-Anwendung geeignete REST-Prinzipien und stellen Sie sicher, dass die Anwendung stateless ist, um eine optimale Skalierbarkeit und Verfügbarkeit im Cloud-Umfeld zu gewährleisten.
- Für die E-Mail-Benachrichtigung können Sie SendGrid, Mailjet oder einen anderen E-Mail-Dienst, der in Azure integriert werden kann, nutzen.
- Überlegen Sie, wie Sie die Idempotenz und Transaktionalität beim Schreiben in den Azure Storage Account und beim Senden von Nachrichten an den Service Bus sicherstellen können.
- **Java-Installation und Anwendungsbereitstellung:** Stellen Sie sicher, dass Java auf den VMs installiert ist und verwenden Sie `ssh`/`scp` für das Kopieren und Starten der Spring-Anwendung.

## Bewertungskriterien

- Funktionalität: Die Spring-Anwendung, Azure Functions und Service Bus Topics/Subscriptions müssen wie beschrieben funktionieren.
- Cloud-natives Design: Die Lösung sollte die Vorteile der Cloud-Infrastruktur nutzen, einschließlich Skalierbarkeit, Verfügbarkeit und Resilienz.
- Codequalität: Klarheit, Wartbarkeit und Einsatz von Best Practices in der Entwicklung.
